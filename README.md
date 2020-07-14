# Munchking

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

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

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can create an account for the platform and log into it from their device.
* Users can create accounts using their Google or Facebook profiles instead of their email. 
* Users can view their own posts and the posts of other people on the app in a "timeline".
* Users can post their own images with text descriptions either from the camera.
* Posts can be tapped on to have a more detailed view of the post.
* Users can comment or like another user's post from the detailed view.
* Users have their own user page that lists their posts
* Users can log off from inside their own user page
* App has animated transitions between each screen

**Optional Nice-to-have Stories**

* Users can tap images in the detailed view to get a closer look at the image and use two fingers to zoom as they see fit.
* Users can filter the posts on their screen by the games they are interested in
* The app has a clean and succinct user interface
* The posts the user previously viewed are persisted when the app is offline
* User page also lists posts the user has liked
* User page lists the perferred games of the user
* User can post images they have saved instead of just from the camera
* Users can view other people's user pages by clicking on their profile images

### 2. Screen Archetypes

* Log-in screen
   * Users can create an account for the platform and log into it from their device.
   * Users can create accounts using their Google or Facebook profiles instead of their email.
* Main screen
   * Users can view their own posts and the posts of other people on the app in a "timeline".
    * Posts can be tapped on to have a more detailed view of the post.
    * Users can filter the posts on their screen by the games they are interested in
    * The posts the user previously viewed are persisted when the app is offline
* Detailed screen
    * Users can comment or like another user's post from the detailed view.
    * Users can view other people's user pages by clicking on their profile images
* Image screen
    * Users can tap images in the detailed view to get a closer look at the image and use two fingers to zoom in/out.
* User screen
    * Users have their own user page that lists their posts
    * Users can log off from inside their own user page
    * User page also lists posts the user has liked
    * User page lists the perferred games of the user
* Posting screen
    * Users can post their own images with text descriptions either from their camera.
    * User can post images they have saved instead of just from the camera.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Tab 1: Main Screen
* Tab 2: Posting Screen
* Tab 3: User Screen

**Flow Navigation** (Screen to Screen)

* Log-in Screen
   * Main Screen (after logging in)
* Main Screen
   * Detailed Screen (click on a post)
   * Log-in Screen (user is not logged in yet)
* Detailed Screen
    * User Screen (Stretch goal, click on profile picture)
    * Image Screen (click on image)
* Image Screen
    * --
* User Screen
    * Detailed Screen (click on a post)
    * Log-in Screen (after logging out)
* Post Screen
    * Main Screen (after posting)

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
