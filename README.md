# Munchking

## Table of Contents
1. [Overview](#Overview)
2. [Product Spec](#Product-Spec)
3. [Screen Archetypes](#Screen-Archetypes)
4. [Navigation](#Navigation)

## Overview
### Description
This app allows its users to upload D&D (or any TTRPG) charPost, browse other user's sheets, and give their opinion on them through likes and comments.

### App Evaluation
- **Category:** Entertainment
- **Mobile:** The app uses its camera to allow for the uploading of images for posting and the creation of profile pictures. Additionally, it allows for push notifications that let users know if their posts were liked/commented on in real time.
- **Story:** People who play Tabletop RPGs cherish their charPosts and hold them in high regard. Whether people want to show off their charPost sheet for a charPost they are playing currently or want to theory craft a charPost to show their fullest potential, people would be more than willing to post.
- **Market:** Tabletop RPGs have blown up in recent years due to the creation of D&D 5th Edition and its large following. In addition to this, celebrities like voice actor Matthew Mercer have begun openly supporting and enjoying the game, making it closer to the mainstream than it has been before.
- **Habit:** Users of this app can both interact with other user's creations or post their own. 
- **Scope:** The app seems reasonable to make in 4 weeks. Even stripped to its bare bones, the app provides many interesting challenges for myself as an app developer.

## Product Spec

**Required Must-have Stories**

* Users can create an account for the platform and log into it from their device.
* Users can create accounts using their Google or Facebook profiles instead of their email. 
* Users can view their own posts and the posts of other people on the app in a "timeline".
* Users can post their own images with text descriptions either from the camera.
* Posts can be tapped on to have a more detailed view of the post.
* Users can comment another user's post from the detailed view.
* Users have their own user page that lists their posts
* Users can log off from inside their own user page
* App has animated transitions between each screen

**Optional Nice-to-have Stories**

* Users can filter the posts on their screen by the games they are interested in
* The app has a clean and succinct user interface
* User page lists the perferred games of the user
* User can post images they have saved instead of just from the camera
* Users can view other people's user pages by clicking on their profile images
* Users can view a map that contains the locations of other users
* Users can add ratings to character posts
* Users can choose to sort their home page by distance, top ratings, or date
* Users can send friend requests to each other and filter their home page by either favorite games or friends

## Screen Archetypes

* Log-in screen
   * Users can create an account for the platform and log into it from their device.
   * Users can create accounts using their Google or Facebook profiles instead of their email.
* Main screen
   * Users can view their own posts and the posts of other people on the app in a "timeline".
    * Posts can be tapped on to have a more detailed view of the post.
    * Users can filter the posts on their screen by the games they are interested in
    * The posts the user previously viewed are persisted when the app is offline
    * Users can change their sorting method or view the map
* Detailed screen
    * Users can comment or like another user's post from the detailed view.
    * Users can view other people's user pages by clicking on their profile images
* User screen
    * Users have their own user page that lists their posts
    * Users can log off from inside their own user page
    * User page also lists posts the user has liked
    * User page lists the perferred games of the user
* Posting screen
    * Users can post their own images with text descriptions either from their camera.
    * User can post images they have saved instead of just from the camera.
* Requests screen
    * Users can see any incoming friend requests and accept or decline them
* Map screen
    * Users can view all active users' locations on map (if permission is given) via a marker
    * Clicking on a marker gives you the option to view the profile screen
* Friends screen
    * User can see the friends of the profile they are viewing
    * Clicking a friend in the list provides their profile
    * Users can unfriend people from here
* Comments screen
    * Allows users to view and post comments
* Settings screen
    * Allows user to change basic settings for their profile

## Navigation

**Tab Navigation** (Tab to Screen)

* Tab 1: Main Screen
* Tab 2: Posting Screen
* Tab 3: Requests Screen
* Tab 4: User Screen

**Flow Navigation** (Screen to Screen)

* Log-in Screen
   * Main Screen (after logging in)
* Main Screen
   * Detailed Screen (click on a post)
   * Log-in Screen (user is not logged in yet)
   * Map Screen
* Detailed Screen
    * User Screen (Stretch goal, click on profile name)
    * Comments screen
* User Screen
    * Detailed Screen (click on a post)
    * Log-in Screen (after logging out)
    * Friends Screen (clicking on friends)
    * Setting Screen (clicking on setting FAB)
* Post Screen
    * Main Screen (after posting)
* Friends Screen
    * User Screen (click on a friend)
* Setting Screen
    * Main Screen (click 'save changes')
    * User Screen (click 'cancel')
