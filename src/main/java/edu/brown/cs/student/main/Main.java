package edu.brown.cs.student.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.*;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * The Main class of our project. This is where execution begins.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static Autocorrector ac;
  private static final Gson GSON = new Gson();

    /**
  * The initial method called when execution begins.
  *
  * @param args
  *          An array of command line arguments
  */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    parser.accepts("prefix");
    parser.accepts("whitespace");
    OptionSpec<Integer> ledSpec = 
      parser.accepts("led").withRequiredArg().ofType(Integer.class);
    OptionSpec<String> dataSpec =
      parser.accepts("data").withRequiredArg().ofType(String.class);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
    if (options.has("data")) {
        boolean prefix = false;
        boolean whitespace = false;
        int led = 0;

        String files = options.valueOf(dataSpec);
        if (options.has("prefix")) {
          prefix = true;
        }
        if (options.has("whitespace")) {
          whitespace = true;
        }
        if (options.has("led")) {
          led = (int) options.valueOf(ledSpec);
        }
        
        // Create autocorrector using files and flags passed in. 
        ac = new Autocorrector(files, prefix, whitespace, led);
        
        // For each line of input from user, output autocorrect suggestions. 
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in))) {
              String input;
              while ((input = br.readLine()) != null) {
                Set<String> suggestions = ac.suggest(input);
                for (String s : suggestions) {
                	System.out.println(s);
                }
              }
              br.close();
            } catch (Exception e) {
            	  System.out.println("ERROR: Invalid input for REPL");
            }
    } else {
        System.out.println("ERROR: usage");
        System.out.print("./run --data=<list of files> \n[--prefix] [--whitespace] [--led=<led>]\n");
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.get("/autocorrect", new AutocorrectHandler(), freeMarker);
    //TODO: create a call to Spark.post to make a post request to a url which
      // will handle getting autocorrect results for the input
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
  
  /** A handler to produce our autocorrect service site.
  *  @return ModelAndView to render. 
  *  (autocorrect.ftl).
  */
  private static class AutocorrectHandler implements TemplateViewRoute {
	  @Override
	  public ModelAndView handle(Request req, Response res) {
	    Map<String, Object> variables = ImmutableMap.of("title",
	        "Autocorrect: Generate suggestions", "message", "Build your Autocorrector here!");
	    return new ModelAndView(variables, "autocorrect.ftl");
	  }
  }

    /** Handles requests for autocorrect on an input
     *  @return GSON which contains the result of autocorrect.suggest()
     */
    private static class ResultsHandler implements Route {
        @Override
        public String handle(Request req, Response res) {
            //TODO: Get JSONObject from req and use it to get the value of the input you want to
            // generate suggestions for

            //TODO: use the global autocorrect instance to get the suggestions

            //TODO: create an immutable map using the suggestions

            //TODO: return a Json of the suggestions (HINT: use the GSON.Json())
            return null;
        }
    }
}
