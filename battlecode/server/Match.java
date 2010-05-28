package battlecode.server;

import java.util.Observable;

import battlecode.common.Team;
import battlecode.engine.*;
import battlecode.serial.*;
//import battlecode.tournament.TournamentType;
//import battlecode.tournament.Match.Type;

import battlecode.world.GameWorldViewer;
import battlecode.world.signal.Signal;

/**
 * Abstracts the game engine for the server. This class is responsible for
 * starting and managing matches and exporting match status and results to the
 * server.
 */
public class Match extends Observable {

	private static int NUM_ARCHON_PER_TEAM = 6;
	
    /** The Engine instance to use to run the game. */
    private Engine engine;

    /** The GameWorldViewer for getting signals. */
    private GameWorldViewer gameWorldViewer;

    /** The MatchInfo from which this match was created. */
    private final MatchInfo info;

    /** The map for this match (one of the maps in info). */
    private final String map;

    /** The command-line/config file options to use. */
    private final Config options;

    private long[][] state = new long[2][NUM_ARCHON_PER_TEAM];

    private int number;

    private int count;
    
    private boolean bytecodesUsedEnabled = true;

	private long[][] computedArchonMemory = null;

    /**
     * Creates a new match with the given parameters and options.
     * 
     * @param info
     *            the teams and map to use when running this match
     * @param options
     *            options relevant to match creation (i.e., default map path)
     */
    public Match(MatchInfo info, String map, Config options, int number,
            int count) {

        this.info = info;
        this.map = map;
        this.options = options;

        this.number = number;
        this.count = count;

        this.engine = null;
        this.gameWorldViewer = null;
    }

    /**
     * Sets up the engine for this match. Because Engine's constructor
     * manipulates static state, engine object creation should not be done at
     * match creation time!
     */
    public void initialize() {
    	
    	boolean debugMethodsEnabled = options
				.getBoolean("bc.engine.debug-methods");

		boolean silenceA = options.getBoolean("bc.engine.silence-a");
		boolean silenceB = options.getBoolean("bc.engine.silence-b");

		boolean gcEnabled = options.getBoolean("bc.engine.gc");
		int gcRounds = options.getInt("bc.engine.gc-rounds");
		boolean upkeepEnabled = options.getBoolean("bc.engine.upkeep");
		boolean spawnRadiusEnforced = options.getBoolean("bc.engine.spawnradius");
		boolean breakpointsEnabled = options.getBoolean("bc.engine.breakpoints");
        this.bytecodesUsedEnabled = 
            options.getBoolean("bc.engine.bytecodes-used"); 
        
		String mapPath = options.get("bc.game.map-path");

		// Create a new engine.
		this.engine = new Engine(info.getTeamA(), info.getTeamB(), map,
				mapPath, debugMethodsEnabled, silenceA, silenceB, gcEnabled,
				gcRounds, upkeepEnabled, spawnRadiusEnforced, breakpointsEnabled, 
                bytecodesUsedEnabled, this.state);

		// Get the viewer from the engine.
		this.gameWorldViewer = engine.getGameWorldViewer();
		assert this.gameWorldViewer != null;
	}

    /**
     * Sends a signal directly to the game engine, possibly altering the match
     * state.
     * 
     * @param signal
     *            the signal to send to the engine
     * @return the signals that represent the effect of the alteration, or an
     *         empty signal array if there was no effect
     */
    public Signal[] alter(Signal signal) {
        if (engine.receiveSignal(signal))
            return gameWorldViewer.getAllSignals(false);
        else
            return new Signal[0];
    }

    /**
     * Determines whether or not this match is ready to run.
     * 
     * @return true if the match has been initialized, false otherwise
     */
    public boolean isInitialized() {
        return this.engine != null;
    }

    /**
     * Runs the next round, returning a delta containing all the signals raised
     * during that round. Notifies observers of anything other than a successful
     * delta-producing run.
     * 
     * @return the signals generated for the next round of the game, or null if
     *         the engine's result was a breakpoint or completion
     */
    public RoundDelta getRound() {

        // Run the next round.
        GameState result = engine.runRound();

        // Notify the server of any other result.
        if (result == GameState.BREAKPOINT) {
            setChanged();
            notifyObservers(result);
            clearChanged();
        }

        if (result == GameState.DONE)
            return null;

        // Serialize the newly modified GameWorld.
        return new RoundDelta(
                gameWorldViewer.getAllSignals(this.bytecodesUsedEnabled));
    }

    /**
     * Queries the engine for stats for the most recent round and returns them.
     * 
     * @return round stats from the engine
     */
    public RoundStats getStats() {
        return gameWorldViewer.getRoundStats();
    }

    /**
     * Queries the engine for stats for the whole match.
     * 
     * @return game stats from the engine
     */
    public GameStats getGameStats() {
        return gameWorldViewer.getGameStats();
    }
    
    /**
     * Gets the header data for this match.
     * 
     * @return this match's header
     */
    public MatchHeader getHeader() {
        return new MatchHeader(gameWorldViewer.getGameMap(), state, number,
                count);
    }
    
    /**
     * Gets team and map metadata for this match.
     * 
     * @return an ExtensibleMetadata with teams and maps
     */
    public ExtensibleMetadata getHeaderMetadata() {
        ExtensibleMetadata ex = new ExtensibleMetadata();
        ex.put("type", "header");
        ex.put("team-a", info.getTeamA());
        ex.put("team-b", info.getTeamB());
        ex.put("maps", info.getMaps());
        return ex;
    }

    /**
     * Gets the footer data for this match.
     * 
     * @return this match's footer
     */
    public MatchFooter getFooter() {
        return new MatchFooter(gameWorldViewer.getWinner(),
                getComputedArchonMemory());
    }

    /**
     * Gets the winner of this match.
     * 
     * @return the Team that has won the match, or null if the match has not yet
     *         finished
     */
    public Team getWinner() {
        if (hasMoreRounds())
            return null;
        return gameWorldViewer.getWinner();
    }

    /**
     * Determines whether or not there are more rounds to be run in this match.
     * 
     * @return true if the match has finished running, false otherwise
     */
    public boolean hasMoreRounds() {
        return engine.isRunning();
    }

    /**
     * Produces a string for the winner of the match.
     * 
     * @return A string representing the match's winner.
     */
    public String getWinnerString() {

        String teamName;

        switch (getWinner()) {
        case A:
            teamName = info.getTeamA() + " (A)";
            break;

        case B:
            teamName = info.getTeamB() + " (B)";
            break;

        default:
            teamName = "nobody";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (50 - teamName.length()) / 2; i++)
            sb.append(' ');
        sb.append(teamName);
        sb.append(" wins");
        
        sb.append("\nReason: ");
        GameStats stats = gameWorldViewer.getGameStats();
        DominationFactor dom = stats.getDominationFactor();
        double[] points = stats.getTotalPoints();
        double[] energon = stats.getTotalEnergon();
        int[] archons = stats.getNumArchons();
        if (dom == DominationFactor.DESTROYED)
        	sb.append("The losing team was destroyed easily.");
        else if (dom == DominationFactor.PWNED)
        	sb.append("The losing team was destroyed.");
        else if (dom == DominationFactor.OWNED)
        	sb.append("Team A had "+(int)points[0]+" points and Team B had "+(int)points[1]+" points, ending the match early");
        else if (dom == DominationFactor.BEAT)
        	sb.append("Team A had "+(int)points[0]+" points and Team B had "+(int)points[1]+" points.");
        else if (dom == DominationFactor.BARELY_BEAT)
        	sb.append("Team A had "+archons[0]+" archons and Team B had "+archons[1]+" archons.");
        else if (dom == DominationFactor.WON_BY_DUBIOUS_REASONS)
        	sb.append("Team A had "+energon[0]+" total energon and Team B had "+energon[1]+" total energon.");
        else
        	sb.append("Team A won by default.");

        return sb.toString();
    }

    public void setInitialArchonMemory(long[][] state) {
        this.state = state;
    }

    public long[][] getInitialArchonMemory() {
        return this.state;
    }

    public long[][] getComputedArchonMemory() {
    	if (computedArchonMemory == null)
    		return this.engine.getArchonMemory();
    	else return computedArchonMemory;
    }

    /**
     * @return the number of the most recently computed round, where the first
     *         round is 1 (0 if no rounds have been run yet)
     */
    public int getRoundNumber() {
        return Engine.getRoundNum() + 1;
    }
    
    /**
     * Cleans up the match so that its resources can be garbage collected.
     */
    public void finish() {
    	this.computedArchonMemory = this.engine.getArchonMemory();
    	this.gameWorldViewer = null;
    	this.engine = null;
    }

    @Override
    public String toString() {
        String teams = String.format("%s vs. %s on %s", info.getTeamA(), info
                .getTeamB(), map);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (50 - teams.length()) / 2; i++)
            sb.append(' ');
        sb.append(teams);

        return sb.toString();
    }
}