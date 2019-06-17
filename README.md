# MovieApp

# Android Components used in the Application.

1. Used MVVM Architecture for the App.
2. Used Room DB for offline Storage.
3. Used LiveData that ensures updating app component observers.

# App Flow
-> Launch the Application 
-> App will fetch all the movie data from MovieDB api.
-> List all the Movies in a Recycler View
-> Sorting can be done according to Popularity, Rating and Favourite.
-> Clicking on a Movie item will take user to Movie Details Page.
-> Movie Details Page will display following details
   - Movie Image
   - Rating
   - Release Year
   - About Movie
   - User can mark Movie as Favourite, it will come under Favourite categorization.
   
