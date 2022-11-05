# SWEN90007-2022-Agora
## Project
Marketplace System
## The Team
Daniel Blain (dblain@student.unimelb.edu.au)\
Christopher Byrnes (byrnesc@student.unimelb.edu.au)\
Gonzalo Molina (gmolina@student.unimelb.edu.au)\
Mengjiao Wei (mengjiaow1@student.unimelb.edu.au)

## Deployment
https://swen90007-agora-marketplace.herokuapp.com/

## Instructions
### Accessing the System
To access the deployment system in an administrator role, use the following credentials:

- Email: admin@agora.com
- Password: Admin_123

Sample user roles are available for use or can be freely created within the platform.

Some sample credentials (email / password) available for use:
- jphillips@mail.net / JP-999
- williams.melissa@mail.com / Furball47
- williams.paul@mail.com / Super_Man38
- rbooth@mail.com / Dodger971
- nsinclair@mail.com / Game!01

Alternatively, when not logged in, click "Register" to sign-up with a new account.

### Making a Listing
1. Login as a standard user (not an admin) and click "View Listings"
2. Click either "Create Fixed-Price Listing" or "Create Auction Listing"
3. Fill in the fields and click the blue submission button
4. The listing will now be viewable in the "View Listings" page

### Browsing Listings
1. Click Search, or navigate to the home page
2. Use the drop-down box to the right of the search bar to select "Item" or "Seller"
3. Type in a search query and hit "Search"
4. Click on a listing to view its page

### Deactivating Listings
As an admin:
- Method 1:
  1. Navigate to a particular listing (see "Browsing Listings")
  2. Click "Remove Listing"
- Method 2:
  1. Navigate to "Listing Management"
  2. Click "Deactivate" on any listing as required

As a standard user:
1. Navigate to "View Listings"
2. Select a listing to view its page
3. Click "Edit"
4. Click "Delete" at the bottom of the page (note: only possible for the listing owner)

### Editing Listings
1. Navigate to "View Listings"
2. Select a listing to view its page
3. Click "Edit"
4. Edit the fields (this cannot be done for auctions which have already started)
5. Click the blue confirmation button to submit to changes

### Bidding on Auctions
1. Navigate to an auction listing (see "Browsing Listings")
2. While the auction is in progress, enter a bid amount higher than the current highest, and click "Bid"

### Purchasing Items
1. Navigate to a fixed-price listing (see "Browsing Listings")
2. Select a quantity to purchase (if stock is available)
3. Click "Purchase"
4. Confirm Shipping Details and click the blue submission button

### Editing Order Details
As a seller:
1. Click "My Sales"
2. Select a fixed-price order
3. Select a new quantity and click the blue submission button to update
As a buyer:
1. Click "My Purchases"
2. Select any order from the list
3. Edit the shipping details (or quantity for a fixed-price listing)
4. Click the blue submission button to update

## Changelog
### [SWEN90007_2022_Part3_Agora](https://github.com/SWEN900072022/SWEN90007-2022-Agora/releases/tag/SWEN90007_2022_Part3_Agora) (2022-10-20)
#### Added
- Concurrency features
  - Read locking on auction listings
  - Read locking on user rows
  - Optimistic locking on listings
  - Optimistic locking on orders
- Concurrency Sequence Diagrams
#### Changed
- Class Diagrams
- Database Query Setup Scripts
- Unit of Work
- Some Controllers (adjusted for locking)
- Mappers (adjusted for locking)
#### Removed
- Coseller Mapper
- Admin Table in Database
### [SWEN90007_2022_Part2_Agora](https://github.com/SWEN900072022/SWEN90007-2022-Agora/releases/tag/SWEN90007_2022_Part2_Agora) (2022-09-21)
#### Added
- Marketplace System source code
  - Mapper classes
  - JSP content files
  - Domain model classes
  - Controller classes
  - Unit of Work class
  - Authentication Enforcer class
- Sequence Diagrams
- Class Diagram
#### Changed
- Use cases
- Domain model
### [SWEN90007_2022_Part1_Agora](https://github.com/SWEN900072022/SWEN90007-2022-Agora/releases/tag/SWEN90007_2022_Part1_Agora) (2022-08-15)
#### Added
- Use cases
- Domain model
#### Changed
- N/A
