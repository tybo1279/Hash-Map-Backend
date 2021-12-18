/**
* Backend for movie sorting program
*
* This program serves as the backend for a Movie Mapper project, which 
* sorts movies and allows an efficient search by rating, genre, title, * duration, and more.
*/
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.DataFormatException;

  @SuppressWarnings({"hiding", "rawtypes"}) 
  public class Backend implements BackendInterface {
    
    public int capacity;
    public int size;
    public double loadFactor;
    public attributesNode[] table;
    public String[] genreList;
    public int genreListSize;
    public List<MovieInterface> movies;
   
    //subclass movieNode that represents the nodes in the array, which each have
    //a LinkedList so that more movieData can be added in case of a hash collision
    class attributesNode<movieData, attributesNode> { 
      
      public movieData data;

      
      private attributesNode (movieData data, attributesNode next) {
        this.data = data;
    }
    }
    
    //subclass movieData that represents the key and the attributes of
    //the movie
    class movieData<Integer, String> { 
      public Integer key;
      public String attributes;
    
    private movieData(Integer key, String attributes) {
      this.key = key;
      this.attributes = attributes;
    }

    }  
    
    public Backend(Reader reader) {
      
      MovieDataReader grab = new MovieDataReader();
      try {
        List<MovieInterface> implement = grab.readDataSet(reader);
        this.movies = implement;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException d) {
        d.printStackTrace();
      } catch (DataFormatException f) {
        f.printStackTrace();
      }
      
      this.capacity = 100;
      this.size = 0;
      this.loadFactor = .85;
      this.table = new attributesNode[capacity];
      this.genreList = new String[100];
      this.genreListSize = 0;
    }

    /**
     * method clears the Hash Table Map
     */
    public void clear() {
      
      //traverses through the array to remove every movieNode
      for (int i = 0; i < capacity; ++i) {
          table[i] = null;
      }
      this.size = 0;
    }
    /**
     * method checks the current LoadFactor of the table
     * @return
     */
    public boolean checkLoadFactor() {
      
      // checks the currrent Load Factor and returns true if it
      //has exceeded the set percentage that would indicate overload
      if (((double)this.size / (double)this.capacity) >= .85) {
        return true;
      }
      else {
      return false;
      }
    }
    
    /**
     * method doubleHashes the table
     */
    private void doubleHash() {
      
      //creates a copy of the existing table, creates a new table
      //with the capacity doubled, and copies elements from the
      //old table into the new table
      attributesNode[] storeTable = this.table;
      this.table = new attributesNode[capacity * 2];
      for (int i = 0; i < capacity; ++i) {
        if (storeTable[i] != null) {
          table[i] = storeTable[i];
        }
      }
      this.capacity = capacity * 2;
    }

    /**
     * Adds a genre to the table as one of the filters
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addGenre(String genre) throws IllegalArgumentException { //DONE
      
      //converts genreList array to ArrayList and checks for duplicates
      List<String> list = new ArrayList<String>(Arrays.asList(genreList));
      if (list.contains(genre)) {
        throw new IllegalArgumentException("genre already in list");
      }
      else {
      list.add(genre);
      }
      //removes null values and sorts genres by alphabetical order
      list.removeAll(Collections.singleton(null));
      this.genreList = list.toArray(new String[list.size()]);
      ++this.genreListSize;
      Arrays.sort(this.genreList);
      
      for (int i = 11; i < genreListSize + 11; ++i) {
        movieData<Integer, String> test = new movieData(i, list.remove(0));
        attributesNode<movieData, attributesNode> insert = new attributesNode(test, null);
        table[i] = insert;
        ++size;
        if (checkLoadFactor()) {
          doubleHash();
        }
      }
    }   

    /**
     * Adds a rating to the table as one of the filters
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addAvgRating(String rating) { //DONE
      
      //adds rating to the corresponding index (example: rating of 1-1.999 stored at index 1 of array)
      Integer index = rating.hashCode() - 48;
      if (index == 1519) {
        index = 10;
      }
      if (index > 10 || index < 0) {
        throw new IllegalArgumentException("invalid rating");
      }
      if (table[index] != null) {
        throw new IllegalArgumentException("rating already set");
      }
      movieData<Integer, String> test = new movieData(index, null);
      attributesNode<movieData, attributesNode> insert = new attributesNode(test, null);
      this.table[index] = insert;
      ++size;
      
    }
    /**
     * Removes a genre from the table as one of the filters
     */
    @SuppressWarnings("unchecked")
    @Override
    public void removeGenre(String genre) throws IllegalArgumentException {
      
      //checks for a match in genre in the array and removes it
      boolean contains = false;
      movieData<Integer, String> test = new movieData(null, genre);
      for (int i = 11; i <= genreListSize + 11 ; ++i) {
        attributesNode<movieData, attributesNode> check = table[i];
        if (check == null) {
          continue;
        }
        movieData<Integer, String> plus = check.data;
          if (plus.attributes.equals(test.attributes)) {
            table[i] = null;
            contains = true;
            --size;
            break;
         }
      }
      //if genre isn't in the table
      if (contains == false) {
        throw new IllegalArgumentException("genre not in the table");
      }
      //update genreList
      for (int i = 0; i <= genreListSize ; ++i) {
        if (genreList[i].equals(genre)) {
          genreList[i] = null;
          --genreListSize;
          break;
        }
      }
      
    }
    /**
     * Removes a rating from the table as one of the filters
     */
    @Override
    public void removeAvgRating(String rating) throws IllegalArgumentException{ //DONE
      
      //removes average rating node in the array
      Integer index = rating.hashCode() - 48;
      if (index > 10 || index < 0) {
        throw new IllegalArgumentException("invalid rating");
      }
      table[index] = null;
      --size;
      
    }
    /**
     * returns a list of all currently set genre filters
     */
    @Override
    public List<String> getGenres() {
      
      //returns genreList as a List
      List<String> listGenres = new ArrayList<String>(Arrays.asList(genreList));
      listGenres.removeAll(Collections.singleton(null));
      
      return listGenres;
    }
    /**
     * returns a list of all currently set average rating filters
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> getAvgRatings() {
      
      //returns List of available average ratings
      List<String> listRatings = new ArrayList<String>();
      for (int i = 0; i <= 10; ++i) {
        attributesNode<movieData, attributesNode> check = table[i];
        if(check != null) {
           movieData<Integer, String> plus = check.data;
           listRatings.add(plus.key.toString());
        }
      }
      
      return listRatings;
    }

    /**
     * returns the number of movies that belong to each of the filters
     */
    @Override
    public int getNumberOfMovies() {
      
      List<String> part1 = getAvgRatings();
      List<String> part2 = getGenres();
      //counts movies that contain the ratings
      int count = 0;
      if (!part1.isEmpty()) {
      for (int i = 0; i < 11; ++i) {
        try {
          part1.get(i);
        }
        catch(IndexOutOfBoundsException e) {
          continue;
        }
        for (int j = 0; j < movies.size(); ++j) {
          String check = movies.get(j).getAvgVote().toString();
          check = check.substring(0, 1);
          if (part1.get(i).equals(check.toString())) {
            ++count;
          }
      }
      }
      }
      //counts movies that contain the genres
     if (!part2.isEmpty()) {
      for (int k = 0; k < genreListSize; ++k) {
        for (int h = 0; h < movies.size(); ++h) {
           List<String> test = movies.get(h).getGenres();
           try {
             test.get(0);
           }
           catch(IndexOutOfBoundsException e) {
             break;
           }
             if (part2.get(k).equals(test.get(0))) {
                ++count;
                test.remove(0);
             }
           }
      }
      } 
       return count;
    }
    /**
     * Returns the total list of Genres in the data file
     */
    @Override
    public List<String> getAllGenres() {
       
       //checks for match of genres in movies field and adds them to the returned list
       List<String> returnList = new ArrayList<String>();
       for (MovieInterface test : movies) {
         for (int i = 0; i < test.getGenres().size(); ++i) {
           String check = test.getGenres().get(i);
           if (check.contains(",")) {
             String[] it = check.split(",");
             for (int k = 0; k < it.length; ++k) {
               it[k] = it[k].substring(1);
               it[k] = removeQuote(it[k]);
               returnList.add(it[k]);
             }
           }
           else {
             returnList.add(check);
           }
         }
       }
       List<String> testList = new ArrayList<String>();
       for (String test : returnList) {
         if (!testList.contains(test)) {
           testList.add(test);
         }
       }
       return testList;
    }
    /**
     * Helper method for getAllGenres
     * @param argument String used to remove qoute from
     * @return a modified String with no white space or qoutation marks
     */
    @SuppressWarnings("static-access")
    public String removeQuote(String argument) {
      
      //helper method for getAllGenres to remove instances of quotes in Strings
      String returnVal = "";
      char[] test = argument.toCharArray();
      for (int i = 0; i < test.length; ++i) {
        if (test[i] == '\"') {
          test[i] = ' ';
        } 
      }
      returnVal = returnVal.valueOf(test);
      returnVal = returnVal.strip();
      return returnVal;
    }
    /**
     * Returns a list of three movies highest to lowest rating that confide
     * to the already set filters
     */
    @SuppressWarnings({"unchecked", "unlikely-arg-type"})
    @Override
    public List<MovieInterface> getThreeMovies(int startingIndex) {
       
      //creates a new list of three movies that have the characteristics of the
      //set genres and the set average ratings, and returns a list of three movies
      //from the new list at the starting index
       List<MovieInterface> returnList = new ArrayList();
       List<String> g = getGenres();
       List<String> r = getAvgRatings();
       
       for (int i = 0; i < g.size(); ++i) {
          for (MovieInterface test: movies) {
             if (test.getGenres().contains(g.get(i))) {
               returnList.add(test);
            }
         }
       } 
       for (int j = 0; j < r.size(); ++j) {
         if (r.get(j) != null) {
           for (MovieInterface test2: movies) {
             if (test2.getAvgVote().intValue() == r.indexOf(j)) {
               returnList.add(test2);
             }
           }
         }
       }
       int count = 0;
       List<MovieInterface> newList = new ArrayList();
       for (int k = startingIndex; k < returnList.size(); ++k) {
          newList.add(returnList.get(k));
          ++count;
          if (count == 3) {
            break;
          }
       }
       
       return newList;
  }
    
  }