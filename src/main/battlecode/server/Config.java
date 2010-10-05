package battlecode.server;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;

/**
 * Represents a game configuration as provided by configuration files and
 * command-line arguments.
 */
public class Config {

    public static final String VERSION = "1.1.12";
    /** Default configuration options. */
    private static final Properties defaults;
    /** Command-line options (uses Apache CLI). */
    private static final Options options;

    // Add/edit config defaults and command-line options here.
    static {

        // Default configuration options.
        defaults = new Properties();

        defaults.setProperty("bc.server.port", "6370");
        defaults.setProperty("bc.server.mode", "local");
        defaults.setProperty("bc.server.save-file", "match.rms");
        defaults.setProperty("bc.server.transcribe-input", "match.rms");
        defaults.setProperty("bc.server.transcribe-output", "transcribed.txt");
        defaults.setProperty("bc.server.debug", "false");
        defaults.setProperty("bc.server.throttle", "yield");
        defaults.setProperty("bc.server.throttle-count", "15");
		defaults.setProperty("bc.server.output-xml", "false");

        defaults.setProperty("bc.engine.debug-methods", "true");
        defaults.setProperty("bc.engine.silence-a", "false");
        defaults.setProperty("bc.engine.silence-b", "false");
        defaults.setProperty("bc.engine.gc", "false");
        defaults.setProperty("bc.engine.gc-rounds", "50");
        defaults.setProperty("bc.engine.upkeep", "true");
        defaults.setProperty("bc.engine.breakpoints", "true");
        defaults.setProperty("bc.engine.bytecodes-used", "true");

        defaults.setProperty("bc.client.opengl", "true");
        defaults.setProperty("bc.client.use-models", "true");
        defaults.setProperty("bc.client.applet", "false");
        defaults.setProperty("bc.client.applet.path", "http://battlecode.mit.edu/2010/online-client/");
	defaults.setProperty("bc.client.renderprefs2d", "");
        defaults.setProperty("bc.client.renderprefs3d", "");
        defaults.setProperty("bc.client.sound-on", "true");
        
	defaults.setProperty("bc.game.team-a", "team000");
        defaults.setProperty("bc.game.team-b", "team000");
        defaults.setProperty("bc.game.maps", "glass");
        defaults.setProperty("bc.game.map-path", "maps");
        defaults.setProperty("bc.game.state", "0,0");


        defaults.setProperty("bc.dialog.skip", "false");




        // Command-line options.
        options = new Options();

        options.addOption("c", "config", true, "configuration file name");
        options.addOption("h", "headless", false, "headless mode");
        options.addOption("s", "server", false, "server mode");
        options.addOption("n", "no-dialog", false, "skip the match dialog");
    }
    private static Config globalConfig = null;

    public static void setGlobalConfig(Config config) {
        globalConfig = config;
    }

    public static Config getGlobalConfig() {
        return globalConfig;
    }
    /** Holds the configuration options provided by file and command line. */
    private final Properties properties;

    /**
     * Creates a new Config instance from the given command-line args.
     *
     * @param args
     *     the command-line arguments to add to the configuration
     */
    public Config(String[] args) {

        // Set up local properties instance.
        properties = new Properties(defaults);

        for (Enumeration propertyNames = System.getProperties().propertyNames(); propertyNames.hasMoreElements();) {
            String s = (String) propertyNames.nextElement();
            if (s.startsWith("bc."))
            {
                properties.setProperty(s, System.getProperty(s));
//                System.out.println(s + " " + System.getProperty(s));
            }
            if (s.startsWith("drw."))
            {
            	properties.setProperty(s, System.getProperty(s));
//            	System.out.println(s + " " + System.getProperty(s));
            }
        }

        addArgs(args);
    }

    /**
     * Processes command-line arguments, converting them to properties and
     * adding them to the local options.
     *
     * @param args
     *     the set of command-line arguments to process
     */
    private void addArgs(String[] args) {

        // Parse them GNU-style. This allows both long and short switches
        // with arguments, i.e. "-c configfile" or "--config configfile".
        CommandLineParser cmdParser = new GnuParser();
        CommandLine cmd = null;
        try {
            cmd = cmdParser.parse(options, args);
        } catch (ParseException e) {
            new HelpFormatter().printHelp("[app]", options);
            throw new IllegalArgumentException();
        }

        // Use the specified configuration file, if any.
        if (cmd.hasOption("c"))
            addFile(cmd.getOptionValue("c"));

        if (cmd.hasOption("h"))
            properties.setProperty("bc.server.mode", "headless");

        if (cmd.hasOption("s"))
            properties.setProperty("bc.server.mode", "tcp");

        if (cmd.hasOption("n"))
            properties.setProperty("bc.dialog.skip", "true");
    }

    /**
     * Adds the configuration file with given filename to this set of
     * configuration options.
     */
    public void addFile(String filename) {
        try {
            FileInputStream f = new FileInputStream(filename);
            properties.load(f);
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a configuration from this set of options.
     *
     * @param key
     *     the option to get
     * @return
     *     that option's value
     */
    public String get(String key) {
        return this.properties.getProperty(key);
    }

    public void set(String key,String value) {
        this.properties.setProperty(key,value);
    }
    /** Gets a configuration value (as an integer). */
    public int getInt(String key) {
        return Integer.valueOf(this.properties.getProperty(key, "0"));
    }

    /** Gets a configuration value (as a boolean). */
    public boolean getBoolean(String key) {
        return Boolean.valueOf(this.properties.getProperty(key, "false"));
    }

    public void setBoolean(String key, boolean value) {
        this.properties.setProperty(key, String.valueOf(value));
    }
}