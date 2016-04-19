// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package profiler;

import com.clarkparsia.pellet.sparqldl.jena.SparqlDLExecutionFactory;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.resultset.ResultSetMem;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.JenaLoader;
import org.mindswap.pellet.utils.FileUtils;
import org.mindswap.pellet.utils.MemUtils;
import org.mindswap.pellet.utils.Timer;
import org.mindswap.pellet.utils.Timers;
import org.mindswap.pellet.utils.VersionInfo;

/**
 * @author Evren Sirin
 */
public class ProfileQuery
{
	private boolean detailedTime = false;
	private boolean printQueryResults = false;
	private boolean classify = false;
	private boolean realize = false;
	private boolean printQuery = false;
	private boolean sizeEstimateAll = false;
	private int maxIteration = 1;
	private final Timers timers = new Timers();
	private final JenaLoader loader = new JenaLoader();
	private OntModel model = null;

	private final ResultList<String> results = new ResultList<>(1, 8);

	public ProfileQuery()
	{
	}

	public boolean isPrintQuery()
	{
		return printQuery;
	}

	public void setPrintQuery(final boolean printQuery)
	{
		this.printQuery = printQuery;
	}

	public boolean isClassify()
	{
		return classify;
	}

	public void setClassify(final boolean classify)
	{
		this.classify = classify;
	}

	public boolean isRealize()
	{
		return realize;
	}

	public void setRealize(final boolean realize)
	{
		this.realize = realize;
	}

	public boolean isPrintQueryResults()
	{
		return printQueryResults;
	}

	public void setPrintQueryResults(final boolean quiet)
	{
		this.printQueryResults = quiet;
	}

	public boolean isDetailedTime()
	{
		return detailedTime;
	}

	public void setDetailedTime(final boolean detailedTime)
	{
		this.detailedTime = detailedTime;
	}

	public boolean isSizeEstimateAll()
	{
		return sizeEstimateAll;
	}

	public void setSizeEstimateAll(final boolean sizeEstimateAll)
	{
		this.sizeEstimateAll = sizeEstimateAll;
	}

	public int getMaxIteration()
	{
		return maxIteration;
	}

	public void setMaxIteration(final int maxIteration)
	{
		this.maxIteration = maxIteration;
	}

	public void profile(final String[] dataset, final String queryset) throws Exception
	{
		Timer t = null;

		final String dataName = dataset[0];

		final Map<String, Query> queries = readQueries(queryset);

		final KnowledgeBase kb = loadData(dataset);

		final double parseTime = timers.getTimerTotal("parse") / 1000.0;
		final double consTime = timers.getTimerTotal("consistency") / 1000.0;

		double classifyTime = 0;
		double realizeTime = 0;
		double sizeEstimateTime = 0;

		if (isSizeEstimateAll())
		{
			t = timers.startTimer("sizeEstimateAll");
			kb.getSizeEstimate().computeAll();
			t.stop();

		}

		System.out.println("Parsing/Loading  : " + parseTime);
		System.out.println("Consistency      : " + consTime);

		if (isClassify())
		{
			classifyTime = timers.getTimerTotal("classify") / 1000.0;
			System.out.println("Classify         : " + classifyTime);
		}

		if (isRealize())
		{
			realizeTime = timers.getTimerTotal("realize") / 1000.0;
			System.out.println("Realize         : " + realizeTime);
		}

		if (isSizeEstimateAll())
		{
			sizeEstimateTime = timers.getTimerTotal("sizeEstimateAll") / 1000.0;
			System.out.println("Size Estimate   : " + sizeEstimateTime);
		}

		final double totalSetupTime = consTime + sizeEstimateTime + classifyTime + realizeTime;
		System.out.println("Total Setup      : " + totalSetupTime);

		for (int i = 0; i < maxIteration; i++)
		{
			System.out.println("\n\n\nITERATION: " + (i + 1) + "\n\n\n");

			final Collection<Result<String>> currResult = new ArrayList<>();

			currResult.add(new Result<>("consistency", consTime));
			if (isClassify())
				currResult.add(new Result<>("classify", classifyTime));
			if (isRealize())
				currResult.add(new Result<>("realize", realizeTime));
			if (isSizeEstimateAll())
				currResult.add(new Result<>("estimate", sizeEstimateTime));
			// currResult.add( new Result<String>( "setup", totalSetupTime ) );

			for (final Map.Entry<String, Query> entry : queries.entrySet())
			{
				final String queryName = entry.getKey();
				final Query query = entry.getValue();

				if (queries.size() > 1)
					System.out.println("Query: " + queryName);

				if (printQuery)
					System.out.println(query);

				t = timers.startTimer("query");

				final QueryExecution queryExec = SparqlDLExecutionFactory.create(query, model);

				final ResultSet queryResults = queryExec.execSelect();

				// we need to consume the results to completely answer the query
				final ResultSetMem resultMem = new ResultSetMem(queryResults);

				final int count = resultMem.size();

				t.stop();

				final double queryTime = t.getLast() / 1000.0;

				if (printQueryResults)
					ResultSetFormatter.out(resultMem, model);

				System.out.println("Query time: " + queryTime);
				System.out.println("Number of results: " + count);
				System.out.println();

				currResult.add(new Result<>(queryName, queryTime));
			}

			results.addResult(dataName, currResult);

			System.out.println("\n\n\nRESULT " + (i + 1) + ":");
			System.out.println("Version: " + VersionInfo.getInstance().getVersionString());
			results.print();
		}

		if (detailedTime)
		{
			System.out.println();
			System.out.println("Detailed timing about reasoner internals:");
			kb.timers.print();
		}
	}

	public KnowledgeBase loadData(final String[] dataset)
	{
		Timer t = timers.startTimer("parse");

		loader.parse(dataset);
		model = loader.getModel();

		System.out.println();
		System.out.println("Triples        : " + model.getBaseModel().size());

		t.stop();

		final KnowledgeBase kb = loader.getKB();

		t = timers.startTimer("load");
		model.prepare();
		t.stop();

		ProfileUtils.printCounts(kb);

		t = timers.startTimer("consistency");
		kb.isConsistent();
		t.stop();

		ProfileUtils.printCounts(kb.getABox());

		if (classify)
		{
			t = timers.startTimer("classify");

			kb.classify();

			t.stop();
		}

		if (realize)
		{
			t = timers.startTimer("realize");

			kb.realize();

			t.stop();
		}

		return kb;
	}

	public Map<String, Query> readQueries(final String loc) throws Exception
	{
		final Map<String, Query> queries = new LinkedHashMap<>();

		final Collection<String> files = FileUtils.getFileURIsFromRegex(loc);
		System.out.print("Parsing (" + files.size() + ") query files...");
		for (final String queryFile : files)
		{
			final String queryName = ProfileUtils.formatFileName(queryFile, 10);
			final String queryStr = FileUtils.readURL(new URL(queryFile));
			final Query query = QueryFactory.create(queryStr);
			queries.put(queryName, query);

			System.out.print(".");
		}
		System.out.println("done.");

		return queries;
	}

	public static void usage()
	{
		System.out.println("PelletQuery");
		System.out.println("");
		System.out.println("Profile the query answering time on a set of datasets. For each");
		System.out.println("daaset, data is loaded once and then all queries are");
		System.out.println("executed consecutively.");
		System.out.println("");
		System.out.println("usage: java ProfileQuery OPTIONS ");
		System.out.println("  -p            Print the query before printing answers");
		System.out.println("  -n            Print only the number of answers not the actual answers");
		System.out.println("  -t            Print detailed time information");
		System.out.println("  -c            Classify the KB before answering queries.");
		System.out.println("  -r            Classify and realize all the instances");
		System.out.println("  {-h,-help}    Print this information");
	}

	public int parseArgs(final String[] args) throws Exception
	{
		final LongOpt[] longopts = new LongOpt[7];
		longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[1] = new LongOpt("print-query", LongOpt.NO_ARGUMENT, null, 'q');
		longopts[1] = new LongOpt("print-results", LongOpt.NO_ARGUMENT, null, 'R');
		longopts[3] = new LongOpt("timing", LongOpt.NO_ARGUMENT, null, 't');
		longopts[4] = new LongOpt("classify", LongOpt.NO_ARGUMENT, null, 'c');
		longopts[4] = new LongOpt("realize", LongOpt.NO_ARGUMENT, null, 'r');
		longopts[5] = new LongOpt("estimate-all", LongOpt.NO_ARGUMENT, null, 'e');
		longopts[6] = new LongOpt("max-iteration", LongOpt.REQUIRED_ARGUMENT, null, 'm');

		final Getopt g = new Getopt(ProfileKB.class.toString(), args, "hqRtcrem:", longopts);

		try
		{
			int c;
			while ((c = g.getopt()) != -1)
				switch (c)
				{
					case 'h':
						usage();
						System.exit(0);

					case 'q':
						setPrintQuery(true);
						break;

					case 'R':
						setPrintQueryResults(true);
						break;

					case 't':
						setDetailedTime(true);
						break;

					case 'c':
						setClassify(true);
						break;

					case 'r':
						setClassify(true);
						setRealize(true);
						break;

					case 'e':
						setSizeEstimateAll(true);
						break;

					case 'm':
						setMaxIteration(Integer.parseInt(g.getOptarg()));
						break;

					case '?':
						ProfileUtils.error("The option '" + (char) g.getOptopt() + "' is not valid");

					default:
						ProfileUtils.error("Unrecognized option: " + (char) c);
				}
		}
		catch (final NumberFormatException e)
		{
			ProfileUtils.error("Invalid number: " + e);
		}

		return g.getOptind();
	}

	public static void main(final String[] args)
	{
		try
		{
			final ProfileQuery profiler = new ProfileQuery();

			final int nextArg = profiler.parseArgs(args);

			final BufferedReader in = new BufferedReader(new FileReader(args[nextArg]));
			String line = null;

			while ((line = in.readLine()) != null && line.length() > 0)
			{
				final String[] dataset = line.split(" ");
				final String queries = in.readLine();

				profiler.profile(dataset, queries);
			}

			MemUtils.runGC();
		}
		catch (final Throwable t)
		{
			t.printStackTrace();
		}
	}
}
