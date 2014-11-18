package battlecode.common;


/**
 * A RobotController allows contestants to make their robot sense and interact
 * with the game world. When a contestant's <code>RobotPlayer</code> is
 * constructed, it is passed an instance of <code>RobotController</code> that
 * controls the newly created robot.
 *
 * @author Teh Devs
 */
public interface RobotController {

    // *********************************
    // ****** GLOBAL QUERY METHODS *****
    // *********************************

    /**
     * Returns the current game map's width.
     *
     * @return the current map's width.
     */
    public int getMapWidth();
   
    /**
     * Returns the current game map's height.
     *
     * @return the current map's height.
     */
    public int getMapHeight();

    /**
     * Checks whether a given upgrade has been researched and is available.
     *
     * @param upgrade the upgrade to check.
     * @return whether a given upgrade is available.
     */
    public boolean hasUpgrade(Upgrade upgrade);

    /**
     * Gets the team's total ore.
     *
     * @return the team's total ore.
     */
    public double getTeamOre();

    /**
     * Counts how many of a certain type of unit you own.
     *
     * @param type the RobotType to query.
     * @return the number of a certain type you own.
     */
    public int getRobotTypeCount(RobotType type);

    /**
     * Returns whether the team has a commander.
     *
     * @return whether the team has a commander.
     */
    public boolean hasCommander();

    // *********************************
    // ****** UNIT QUERY METHODS *******
    // *********************************

    /**
     * Use this method to access your ID.
     *
     * @return the ID of the robot.
     */
    public int getID();

    /**
     * Gets the Team of this robot.
     *
     * @return this robot's Team
     */
    public Team getTeam();

    /**
     * Gets this robot's type (SOLDIER, HQ, etc.).
     *
     * @return this robot's type.
     */
    public RobotType getType();
    
    /**
     * Gets the robot's current location.
     *
     * @return this robot's current location.
     */
    public MapLocation getLocation();

    /**
     * Returns the number of turns until the unit can move again. If the result is less than 1, then the unit can move.
     *
     * @return the number of turns until the unit can move again.
     */
    public double getTurnsUntilMovement();

    /**
     * Returns the number of turns until the unit can attack again. If the result is less than 1, then the unit can attack.
     *
     * @return the number of turns until the unit can attack again.
     */
    public double getTurnsUntilAttack();

    /**
     * Gets the robot's current health.
     *
     * @return this robot's current health.
     */
    public double getHealth();

    /**
     * Gets the robot's current suplly level.
     *
     * @return this robot's supply leve.
     */
    public double getSupplyLevel();

    /**
     * Gets the experience a robot has. Only meaningful for COMMANDER.
     *
     * @return the number of XP the unit has.
     */
    public int getXP();

	/**
     * Returns whether the robot is currently building a building.
     *
     * @return whether this robot is currently building a building.
     */
    public boolean isBuildingSomething();
	
	/**
     * Returns the RobotType of the building the robot is building.
     *
     * @return the RobotType of the building this robot is building.
     */
    public RobotType getBuildingTypeBeingBuilt();
	
	/**
     * Returns the number of turns left in the current building.
     *
     * @return the number of turns left in the current building.
     */
    public int getBuildingRoundsRemaining();

    /**
     * Returns how many missiles the unit has. Only useful for launcher.
     *
     * @return the number of missiles the unit has.
     */
    public int getMissileCount();

    // ***********************************
    // ****** GENERAL SENSOR METHODS *****
    // ***********************************
    
    /**
     * Returns location of the allied team's HQ (unconstrained by sensor range or distance).
     * @return the team's HQ location.
     */
    public MapLocation senseHQLocation();

    /**
     * Returns location of the enemy team's HQ (unconstrained by sensor range or distance).
     *
     * @return the enemy team's HQ location.
     */
    public MapLocation senseEnemyHQLocation();

    /**
     * Senses the terrain at the given location. Returns TerrainTile.UNKNOWN for a terrain tile that has never been in sensor range.
     *
     * @param loc the location to check.
     * @return the TerrainTile at the location.
     */
    public TerrainTile senseTerrainTile(MapLocation loc);

    /**
	 * Returns true if the given object is within the robot's sensor range.
     *
     * @param o the game object to check.
     * @return whether the given object is within the robot's sensor range.
     */
    public boolean canSenseObject(GameObject o);

    /**
     * Returns true if the given location is within the robot's sensor range.
     *
     * @param loc the location to check.
     * @return whether the given location is within the robot's sensor range.
     */
    public boolean canSenseSquare(MapLocation loc);
    
    /**
     * Sense the location of the given object.
     *
     * @param o the object to check
     * @return the location of the given object.
     * @throws GameActionException if object is not within sensor range (CANT_SENSE_THAT).
     */
    public MapLocation senseLocationOf(GameObject o) throws GameActionException;

    /**
     * Returns the object at the given location, or <code>null</code>
     * if there is no object there.
     *
     * @param loc the location to check.
     * @return the object at the given location.
     * @throws GameActionException if <code>loc</code> is not within sensor range (CANT_SENSE_THAT).
     */
    public GameObject senseObjectAtLocation(MapLocation loc) throws GameActionException;

    /** 
     * Returns all robots of a given type nearby the robot.
     *
     * @see #senseNearbyRobots(MapLocation, int, Team).
     */
    public RobotInfo[] senseNearbyRobots();

    /**
     * Returns all robots of a given type nearby the robot.
     *
     * @see #senseNearbyRobots(MapLocation, int, Team).
     */
    public RobotInfo[] senseNearbyRobots(int radiusSquared);
    
    /**
     * Returns all robots of a given type nearby the robot of a given team.
     *
     * @see #senseNearbyRobot(MapLocation, int, Team).
     */
    public RobotInfo[] senseNearbyRobots(int radiusSquared, Team team);
    
    /**
     * Senses all robots of a given type within a given search area specified by the parameters (constrainted by sensor range and distance).
     *
     * @param center center of the given search radius.
     * @param radiusSquared return objects this distance away from the center.
     * @param team filter game objects by the given team. If null is passed, objects from all teams are returned.
     * @return array of class type of game objects.
     */
    public RobotInfo[] senseNearbyRobots(MapLocation center, int radiusSquared, Team team);

    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************

    /**
     * Returns whether the robot is able to move in the current turn. Essentially, it checks whether the number of turns until movement is less than 1.
     *
     * @return whether the robot is able to move in the current turn.
     */
    public boolean isMovementActive();

    /**
     * Tells whether this robot can move in the given direction. Takes into
     * account only the map terrain and positions of other robots. Does not take
     * into account this robot's type or whether this robot is currently active.
     * Returns false for the OMNI and NONE directions.
     *
     * @return true if there are no robots or walls preventing this robot from
     *         moving in the given direction; false otherwise.
     */
    public boolean canMove(Direction dir);

    /**
     * Move in the given direction if possible.
     *
     * @param dir the direction to move in.
     * @throws GameActionException if the robot cannot move in this direction.
     */
    public void move(Direction dir) throws GameActionException;

    // ***********************************
    // ****** ATTACK METHODS *************
    // ***********************************

    /**
     * Returns whether the robot is able to attack in the current turn. Essentially, it checks whether the number of turns until attack is less than 1.
     *
     * @return whether the robot is able to attack in the current turn.
     */
    public boolean isAttackActive();

    /**
     * Returns whether the given location is within the robot's attack range. Does not take into account whether the robot is currently attacking.
     *
     * @return true if the given location is within this robot's attack range.
     * Does not take into account whether the robot is currently attacking.
     */
    public boolean canAttackSquare(MapLocation loc);

    /**   
     * Attacks the given location.
     *
     * @param loc the location to attack.
     * @throws GameActionException if the robot cannot attack the given square.
     */
    public void attackSquare(MapLocation loc) throws GameActionException;

    /**
     * BASHERS ONLY. Attacks all surrounding enemies.
     *
     * @throws GameActionException if the robot is not a BASHER or if attack is not allowed.
     */
    public void bash() throws GameActionException;

    /**
     * MISSILE ONLY. Attacks all surrounding enemies.
     *
     * @throws GameActionException if the robot is not a MISSILE or if attack is not allowed.
     */
    public void explode() throws GameActionException;

    // ***********************************
    // ****** BROADCAST METHODS **********
    // ***********************************
    
    /**
     * Broadcasts a message to the global message board.
     * The data is not written until the end of the robot's turn.
     *
     * @param channel the channel to write to, from 0 to <code>BROADCAST_MAX_CHANNELS</code>.
     * @param data one int's worth of data to write.
     * @throws GameActionException if the channel is invalid.
     */
    public void broadcast(int channel, int data) throws GameActionException;

    /**
     * Retrieves the message stored at the given radio channel.
     *
     * @param channel radio channel to query, from 0 to <code>BROADCAST_MAX_CHANNELS</code>.
     * @return data currently stored on the channel.
     * @throws GameActionException if the channel is invalid.
     */
    public int readBroadcast(int channel) throws GameActionException;

    // ***********************************
    // ****** SUPPLY METHODS *************
    // ***********************************

    /**
     * Senses the amount of supply at a given location.
     *
     * @param loc the location to sense.
     * @return the amount of supply at the location.
     * @throws GameActionException if the location can't be sensed.
     */
    public double senseSupplyLevelAtLocation(MapLocation loc) throws GameActionException;

    /**
     * Drops supplies to the current location the robot is standing on.
     *
     * @param amount the amount of supply to drop.
     * @throws GameActionException if there isn't enough supply to drop.
     */
    public void dropSupplies(int amount) throws GameActionException;

    /**
     * Transfers supplies to a robot in the given direction.
     *
     * @param amount the amount of supply to transfer.
     * @param dir the direction to transfer the supply
     * @throws GameActionException if there isn't enough supply or if there is no one to transfer to.
     */
    public void transferSupplies(int amount, Direction dir) throws GameActionException;

    /**
     * Picks up supplies from the location the robot is standing on.
     *
     * @param amount the amount of supply to pick up.
     * @throws GameActionException if there isn't enough supply to pick up.
     */
    public void pickUpSupplies(int amount) throws GameActionException;

    /**
     * SUPPLYDEPOT ONLY. Transfers all supplies to HQ.
     *
     * @throws GameActionException if not a SUPPLYDEPOT.
     */
    public void transferSuppliesToHQ() throws GameActionException;

    // ***********************************
    // ****** MINING METHODS *************
    // ***********************************

    /**
     * Returns the amount of ore at a given location. If the location is out of sensor range, this returns the last known ore amount at that location. If the location is off the map or is void, then 0 is returned. If the location has never been in sensor range, then -1 is returned.
     *
     * @param loc the MapLocation to sense ore at.
     * @return the amount of ore at a given location. If the location is out of sensor range, then the last known ore amount is returned.
     */
    public int senseOre(MapLocation loc) throws GameActionException;

    /**
     * Mines the current square for ore.
     *
     * @throws GameActionException if the current unit is not one that can collect ore
     * @throws GameActionException if there is currently movement delay and the unit cannot mine
     */
    public void mine() throws GameActionException;

    // ***********************************
    // ****** BUILDING/SPAWNING **********
    // ***********************************

    /**
     * LAUNCHER ONLY. Launches a missile in the given direction.
     *
     * @param dir the direction to launch a missile.
     * @throws GameActionException if not enough missiles or otherwise can't attack.
     */
    public void launchMissile(Direction dir) throws GameActionException;

    /**
     * Returns whether the unit can spawn a robot in the given direction of the given type.
     * Checks dependencies and ore costs. Does not check if a robot is active.
     *
     * @param dir the direction to spawn in.
     * @param type the robot type to spawn.
     */
    public boolean canSpawn(Direction dir, RobotType type);

    /**
     * Queues a spawn action to be performed at the end of this robot's turn.
     * When the action is executed, a new robot will be created adjacent to the HQ
     * in the given direction.  The square must not already be occupied.
     * The new robot is created and starts executing bytecodes immediately
     *
     * @param dir the direction to spawn robot in.
     * @param type the robot type to spawn.
     * @throws GameActionException if bad.
     */
    public void spawn(Direction dir, RobotType type) throws GameActionException;

    /**
     * Returns whether the unit can build a building in the given direction of the given type.
     * Checks dependencies and ore costs. Does not check if a robot is active.
     *
     * @param dir the direction to build in.
     * @param type the robot type to build.
     */
    public boolean canBuild(Direction dir, RobotType type);
   
    /**
     * Builds a building in the given direction. The building will initially be inactive for a number of turns (during which this robot cannot move or attack). After several turns, the building will become active.
     *
     * @param dir the direction to bulid in.
     * @param type the type to build.
     * @throws GameActionException if bad.
     */
    public void build(Direction dir, RobotType type) throws GameActionException;

    // ***********************************
    // ****** UPGRADE METHODS ************
    // ***********************************

    /**
     * Researches the given upgrade for a turn.
     *
     * @param upgrade the upgrade to research.
     * @throws GameActionException if can't research.
     */
    public void researchUpgrade(Upgrade upgrade) throws GameActionException;
    
    /**
     * Checks the total number of rounds a given research has been researched
     *
     * @param upgrade the upgrade to check.
     * @return how many turns have been spent on the upgrade.
     * @throws GameActionException if bad.
     */
    public int checkResearchProgress(Upgrade upgrade) throws GameActionException;

    // ***********************************
    // ****** OTHER ACTION METHODS *******
    // ***********************************
    
    /**
     * Ends the current round.
     * Never fails.
     */
    public void yield();

    /**
     * Kills your robot and ends the current round. Never fails. Drops supplies on the ground.
     */
    public void disintegrate();

    /**
     * Causes your team to lose the game. It's like typing "gg."
     */
    public void resign();

    public void win();

    // ***********************************
    // ******** MISC. METHODS ************
    // ***********************************
    
    /**
     * Puts a hat on the robot. You require the BATTLECODE-HATS DLC. You also cannot be moving while putting on your hat. This costs ore (GameConstants.HAT_ORE_COST). The HQ's first hat is free.
     *
     * @throws GameActionException if you have action delay or if you do not have enough ore.
     */
    public void wearHat() throws GameActionException;

    /**
     * Sets the team's "memory", which is saved for the next game in the
     * match. The memory is an array of {@link GameConstants#TEAM_MEMORY_LENGTH}
     * longs.  If this method is called more than once with the same index
     * in the same game, the last call is what is saved for the
     * next game.
     *
     * @param index the index of the array to set.
     * @param value the data that the team should remember for the next game.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *          if {@code index} is less
     *          than zero or greater than or equal to {@link GameConstants#TEAM_MEMORY_LENGTH}.
     * @see #getTeamMemory
     * @see #setTeamMemory(int, long, long)
     */
    public void setTeamMemory(int index, long value);

    /**
     * Sets this team's "memory". This function allows for finer control
     * than {@link #setTeamMemory(int, long)} provides.  For example,
     * if {@code mask == 0xFF} then only the eight least significant bits of
     * the memory will be set.
     *
     * @param index the index of the array to set.
     * @param value the data that the team should remember for the next game.
     * @param mask  indicates which bits should be set.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *          if {@code index} is less
     *          than zero or greater than or equal to {@link GameConstants#TEAM_MEMORY_LENGTH}.
     * @see #getTeamMemory
     * @see #setTeamMemory(int, long)
     */
    public void setTeamMemory(int index, long value, long mask);

    /**
     * Returns the team memory from the  last game of the match.
     * The return value is an array of length {@link GameConstants#TEAM_MEMORY_LENGTH}.
     * If setTeamMemory was not called in the last game, or there was no last game, the
     * corresponding long defaults to 0.
     *
     * @return the team memory from the the last game of the match.
     * @see #setTeamMemory(int, long)
     * @see #setTeamMemory(int, long, long)
     */
    public long[] getTeamMemory();

    // ***********************************
    // ******** DEBUG METHODS ************
    // ***********************************

    /**
     * Sets one of this robot's 'indicator strings' for debugging purposes.
     * These strings are displayed in the client. This method has no effect on
     * gameplay (aside from the number of bytecodes executed to call this
     * method).
     *
     * @param stringIndex the index of the indicator string to set. Must satisfy
     *                    <code>stringIndex >= 0 && stringIndex < GameConstants.NUMBER_OF_INDICATOR_STRINGS</code>.
     * @param newString  the value to which the indicator string should be set.
     */
    public void setIndicatorString(int stringIndex, String newString);

    /**
     * Adds a custom observation to the match file, such that when it is analyzed, this observation will appear.
     *
     * @param observation the observation you want to inject into the match file.
     */
    public void addMatchObservation(String observation);

    /**
     * If breakpoints are enabled, calling this method causes the game engine to
     * pause execution at the end of this round, until the user decides to
     * resume execution.
     */
    public void breakpoint();
}
