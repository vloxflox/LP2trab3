import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * (Fill in description and author info here)
 */
public class Ocean
{

    // A random number generator for providing random locations.
    private static final Random rand = new Random();

    //height of the ocean
    private int height;
    //width of the ocean
    private int width;

    // Storage for the fishes
    private Fish[][] ocean;
    private Algae[][] algae;


    /**
     * Represent an ocean of the given dimensions.
     * @param height The height of the ocean.
     * @param width The width of the ocean.
     */
    public Ocean(int height, int width)
    {
        this.height = height;
        this.width  = width;
        ocean       = new Fish[height][width];
        algae       = new Algae[height][width];
    }
    
    /**
     * Return the fish at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The fish at the given location, or null if there is none.
     */
    public Fish getFishAt(int row, int col)
    {
        return ocean[row][col];
    }

    /**
     * Return the fish at the given location, if any.
     * @param location Encapsulation of the row x col structure
     * @return The fish at the given location, or null if there is none.
     */
    public Fish getFishAt(Location location)
    {
        return getFishAt(location.getRow(), location.getCol());
    }

    /**
     * Return the algae at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The algae at the given location, or null if there is none.
     */
    public Algae getAlgaeAt(int row, int col)
    {
        return algae[row][col];
    }

    /**
     * Return the algae at the given location, if any.
     * @param location Encapsulation of the row x col structure
     * @return The algae at the given location, or null if there is none.
     */
    public Algae getAlgaeAt(Location location)
    {
        return getAlgaeAt(location.getRow(), location.getCol());
    }
    
    /**
     * Adds a fish to the specified location at the ocean matrix
     * @param fish the fish to be added
     * @param location Encapsulation of the row x col structure
     * @return The algae at the given location, or null if there is none.
     */
    public void place(Fish fish, Location location)
    {
        ocean[location.getRow()][location.getCol()] = fish;
    }

    /**
     * Adds an algae to the specified location at the ocean matrix
     * @param newAlgae the algae to be added
     * @param location Encapsulation of the row x col structure
     * @return The algae at the given location, or null if there is none.
     */
    public void place(Algae newAlgae, Location location)
    {
        algae[location.getRow()][location.getCol()] = newAlgae;
    }

    /**
     * Sets all fish objects to null
     */
    public void clear()
    {
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                ocean[row][col] = null;
            }
        }
    }

    /**
     * Sets a fish object at target location to null
     * @param location where to remove the fish
     */
    public void clear(Location location)
    {
        ocean[location.getRow()][location.getCol()] = null;
    }

    /**
     * Returns all free locations adjacent to a spot
     * @param location where to look around for a free location
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<Location>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getFishAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }


    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<Location>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < height) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Searches all free locations that don't have an adjacent shark block next to it calls for all 
     * adjacent blocks to check their adjacencies for a shark (excluding the caller of the function)
     * @param location where to look around for a free location with no sharks around
     * @return all blocks around with no adjacent shark (aside from the caller)
     */
    public List<Location> freeSharklessLocations(Location location)
    {
        List<Location> sharkless = new LinkedList<Location>();

        
        if(location != null) {
            
            //supposes every free location around it is sharkless
            sharkless = getFreeAdjacentLocations(location);

            //Gets all actually free locations to test if they're indeed sharkless
            List<Location> adjacent  = getFreeAdjacentLocations(location);
            for(Location next : adjacent) {
                //checks around every node around it
                List<Location> adjacentInside  = adjacentLocations(next);
                for(Location nextInside : adjacentInside) {
                    //checks if it's a shark other than the caller of the method
                    if(getFishAt(nextInside) instanceof Shark && !nextInside.equals(location)) {
                        //removes the location (from the outside loop) from the sharkless list and goes to the next one
                        sharkless.remove(next);
                        break;
                    }
                }
            }
            //randomizes for use in other functions
            Collections.shuffle(sharkless, rand);
        }
        

        return sharkless;
    }
    
    /**
     * Returns one of the locations that don't have an adjacent shark block next to it
     * @param location where to look around for a free location with no sharks around
     * @return a place with no shark nearby or null if there is none
     */
    public Location getSharklessLocation(Location location)
    {
        List<Location> sharkless = freeSharklessLocations(location);
        if(sharkless.isEmpty()){
            return null;
        }else{
            return sharkless.get(0);
        }
    }

    /**
     * Searches all free locations that have an adjacent sardine block next to it calls for all 
     * adjacent blocks to check their adjacencies for a sardine (excluding the caller of the function)
     * @param location where to look around for a free location with sardines around
     * @return all blocks around with no adjacent sardine (aside from the caller)
     */
    public List<Location> freeSardinefulLocations(Location location)
    {
        List<Location> sardineful = new LinkedList<Location>();

        
        if(sardineful != null) {
            
            //supposes every free location around it is sharkless
            sardineful = getFreeAdjacentLocations(location);

            //Gets all actually free locations to test if they're indeed sharkless
            List<Location> adjacent  = getFreeAdjacentLocations(location);
            for(Location next : adjacent) {
                //checks around every node around it
                List<Location> adjacentInside  = adjacentLocations(next);
                for(Location nextInside : adjacentInside) {
                    //checks if it's a shark other than the caller of the method
                    if(getFishAt(nextInside) instanceof Sardine && !nextInside.equals(location)) {
                        //removes the location (from the outside loop) from the sharkless list and goes to the next one
                        sardineful.add(next);
                        break;
                    }
                }
            }
            //randomizes for use in other functions
            Collections.shuffle(sardineful, rand);
        }
        

        return sardineful;
    }

    /**
     * Returns one of the locations that have an adjacent shark block next to it
     * @param location where to look around for a free location with sardine around
     * @return a place with sardines nearby or null if there is none
     */
    public Location getSardinefulLocation(Location location)
    {
        List<Location> sardineful = freeSardinefulLocations(location);
        if(sardineful.isEmpty()){
            return null;
        }else{
            return sardineful.get(0);
        }
    }

    /**
     * returns a random adjacent location to the target setted at the parameter
     * @param location target location
     * @return a random location around the target
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }


    /**
     * @return The height of the ocean.
     */
    public int getHeight()
    {
        return height;
    }
    
    /**
     * @return The width of the ocean.
     */
    public int getWidth()
    {
        return width;
    }


}
