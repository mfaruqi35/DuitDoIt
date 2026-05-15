# DoitDuit - Design Guidelines

## UI Tone
Clean casual, inspired by Notion/Todoist/Linear. Not too formal like a banking app, not too playful like a kids app.

## Colors

### Primary
```
Primary:        #2563EB / #296AFD / #3771FF / #3C74FC
Primary Dark:   #1D4ED8
Secondary:      #EFF6FF
```

### General
```
Background:     #FFFFFF
Surface:        #F8FAFC
Text Primary:   #0F172A
Text Secondary: #64748B
Border:         #E2E8F0
Income:         #16A34A (green, with green circle icon)
Expense:        #DC2626 (red, with red circle icon)
```

### Transaction Categories - Expense
```
Food & Drinks   #EF4444
Transport       #F97316
Shopping        #A855F7
Leisure         #EC4899
Health          #14B8A6
Education       #3B82F6
Bills           #EAB308
Other           #6B7280
```

### Transaction Categories - Income
```
Salary          #22C55E
Freelance       #10B981
Business        #3771FF
Gift            #EC4899
Other           #6B7280
```

### Regular Payment Categories
```
Streaming       #E50914
Cicilan         #2563EB
Utilitas        #EAB308
Iuran           #16A34A
Software        #8B5CF6
Other           #6B7280
```

### Wishlist Categories
```
Electronics     #2563EB
Fashion         #EC4899
Food            #EF4444
Travel          #06B6D4
Health          #14B8A6
Other           #6B7280
```

### Account Icon Colors
```
Wallet          #2563EB (blue)
Debit/Bank      #EAB308 (yellow)
Savings         #16A34A (green)
E-Wallet        #8B5CF6 (purple)
Other           #6B7280 (grey)
```

### Priority Colors (Wishlist)
```
High            #EF4444
Medium          #F97316
Low             #16A34A
```

### Transfer Icon Color
```
Transfer icon container: #6B7280 (grey)
```

## Typography
- **Font:** Poppins (Google Fonts)
- **Heading:** Poppins SemiBold
- **Body:** Poppins Regular
- **Amount:** Poppins Medium

### Font Sizes
```
Display (main balance)    32sp
Heading (section name)    20sp
Title (transaction name)  16sp
Body (label, note)        14sp
Caption (date, hint)      12sp
Amount large              24sp
Amount small              16sp
```

## Components

### Header
- Background: primary blue with circle/blob pattern
- Title: white, centered, Poppins SemiBold
- Back arrow: white, left side
- Some screens have minimal header (only back arrow, no title): Transaction per Category

### Tab (Expense / Income)
- Style: pill tab
- Active: white background with shadow
- Inactive: transparent grey
- Attached to bottom of header, stretching full width
- Left tab slightly overflows card container to the left

### Cards
- Rounded corner: 16dp
- Shadow: tipis
- Background: white
- On dashboard and transaction form: slightly overlaps header at the top
- Some screens: card does not overlap header (Analytics Cash Flow card)

### Filter Period Chips
- Style: individual pill chips
- Options: Day | Week | Month | Year
- Active: primary blue background, white text
- Inactive: white/light background, dark text

### Period Navigator
- Format: <- label periode >> ->
- <- : go back one period
- -> : go forward one period
- >> : skip to current period

### Date Selector Chips (Transaction & Transfer)
- Each chip shows: DD/M on top, label (Today/Yesterday/Last) below
- Active: primary blue background, white text
- Inactive: white background, grey border, dark text
- Default: Today is active
- Calendar icon on right to open full date picker

### Account Selector / Dropdown
- Collapsed: outlined rounded container, full width, selected name + chevron down
- Expanded: panel below with "Choose Account ▲" header + list of account names

### Category Icon Grid
- Layout: 3 columns
- Icon color: #FFFFFF (all white)
- Icon size: 24dp canvas, scalable
- Background: category color, rounded square
- Unselected: no label on container
- Selected: label appears inside container at bottom

### Input Fields
- Amount: "Rp" bold large prefix + underline only
- Text fields (name, note): underline only, no border box
- Label above input field

### Buttons
- Shape: full width, pill/fully rounded
- Primary action (Add/Transfer): primary blue, full width, at bottom
- Destructive + Edit: Delete (red, left half) + Edit (blue, right half) side by side

### Action Buttons (Accounts screen)
- Style: square outlined container with icon inside, label below
- Icons: History (clock), Add (+), Transfer (swap arrows)

### Account Icon Grid (Add/Edit Account)
- 5 icons in a grid (not 3 columns like category)
- Row 1: Wallet (blue), Debit (yellow), Savings (green), E-Wallet (purple)
- Row 2: Other (grey)
- Selected: border or highlight around icon

### Transfer List Item (History)
```
[icon grey container]  From → To       Rp XXX.XXX
                       Notes
```

### Transaction List Item
```
[icon container]  Category (bold)      Rp XXX.XXX
                  Account | Notes
```

### Transaction per Category List Item
```
[icon container]  Account (bold)       Rp XXX.XXX
                  Notes
```

### Wishlist List Item
```
[icon container]  Wishlist name (bold)              High/Medium/Low (colored)
                  Rp XXX.XXX from Rp XXX.XXX
                  [progress bar, color = category color]
```

### Regular Payment List Item
```
[icon container grey]  Payment name (bold)      Rp XXX.XXX/mo
                       Account | DD Mon YYYY
```

### Add Button (dashed card)
- Style: dashed outlined card, full width
- Text: grey, centered (e.g. "+ Add Wishlist", "+ Add Regular Payment")
- Appears at the bottom of a list as the last item

### Bottom Navigation
- Background: white
- Active icon: primary blue, filled
- Inactive icon: grey, outline
- Center button "+": circle, primary blue, raised above navbar
- Tab order: Dashboard | Transactions | [+] | Accounts | Extras
- Icons:
    - Dashboard      → grid_view (4 squares)
    - Transactions   → bar_chart
    - [+]            → add (always blue circle, raised)
    - Accounts       → wallet
    - Extras         → star

## Screen Layouts

### Dashboard
```
Header (blue, blob pattern):
  "Total ▼" small white
  "Rp XXX.XXX" large white bold

Card (overlaps header):
  Tab: EXPENSE | INCOME
  [Day] [Week] [Month] [Year]
  <- label periode >> ->
  Donut chart (total amount center, 1 decimal)

Section: Upcoming Payments  |  See all
  Horizontal scroll cards:
    [icon container grey]
    Sub name (bold)
    Rp XXX.XXX/month

Section: Latest Transactions  |  See all
  [icon small]  Category (bold)     Rp XXX.XXX
                X Transactions
```

### Analytics (Transactions tab)
```
Header (blue, blob pattern):
  "Analytics"  [filter icon right]

Cash Flow card (does NOT overlap header, below it):
  Cash Flow (bold)
  [green icon] Income      Rp XXX.XXX (green)
  [red icon]   Expense     Rp XXX.XXX (red)
  ─────────────────────────────
  Total:                   Rp XXX.XXX

[Day] [Week] [Month] [Year]  (outside card)

Chart card:
  + Rp XXX.XXX (net, green if positive)
  [bar chart area]

Transactions section:
  Container card:
    <- 2025 ->
    May 2025         ▼ (expanded)
    June 2025        ▶ (collapsed)
      [icon]  Category (bold)   Rp XXX.XXX
              Account | Notes
      [icon]  Category (bold)   Rp XXX.XXX
              Account | Notes
    August 2025      ▶ (collapsed)
```

### Transaction List - Daily
```
Container card:
  <- Thursday, 14 May 2025 ->

  [icon]  Category (bold)     Rp XXX.XXX
          Account | Notes

  [icon]  Category (bold)     Rp XXX.XXX
          Account | Notes
```

### Transaction List - Weekly
```
Container card:
  <- 14 - 21 May 2025 ->

  Wednesday, 14 May 2025
  [icon]  Category (bold)     Rp XXX.XXX
          Account | Notes

  Thursday, 15 May 2025
  [icon]  Category (bold)     Rp XXX.XXX
          Account | Notes
```

### Transaction List - Monthly
```
Container card:
  <- May 2025 ->

  Wednesday, 14 May 2025
  [icon]  Category (bold)     Rp XXX.XXX
          Account | Notes

  Thursday, 15 May 2025
  [icon]  Category (bold)     Rp XXX.XXX
          Account | Notes
```

### Transaction List - Yearly
```
Container card:
  <- 2025 ->

  May 2025         ▼ (expanded, collapsible)
    [icon]  Category (bold)   Rp XXX.XXX
            Account | Notes

  June 2025        ▶ (collapsed)
  August 2025      ▶ (collapsed)
  (months with no transactions are hidden)
```

### Transaction per Category
```
Header (blue, minimal - back arrow only):

Category summary card (overlaps header):
  [icon large]  Category (bold)     Rp XXX.XXX
                X Transactions

14 May 2025
  [icon small]  Account (bold)      Rp XXX.XXX
                Notes

19 May 2025
  [icon small]  Account (bold)      Rp XXX.XXX
                Notes
```

### Add Transaction
```
Header (blue): <- "Add Transaction"

Card (overlaps header):
  Tab: EXPENSE | INCOME

  Rp ___________

  Account
  [Wallet A ▼]

  Category
  [3-column icon grid]

  Date                    [calendar icon]
  [DD/M Today] [DD/M Yesterday] [DD/M Last]

  Note
  ___________

  [          Add          ]
```

### Transaction Detail
```
Header (blue): <- "Transaction"

Card (overlaps header):
  Tab: EXPENSE | INCOME

  Rp ___________

  Account
  [Wallet A ▼]

  Category
  [3-column icon grid]

  Date                    [calendar icon]
  [DD/M Today] [DD/M Yesterday] [DD/M Last]

  Note
  ___________

  [  Delete  ]     [   Edit   ]
```

### Accounts
```
Header (blue, blob pattern):
  "Accounts"

Total card:
  Total
  Rp XXX.XXX (bold, large)

Action buttons row:
  [clock icon]   [+ icon]   [swap icon]
  History        Add        Transfer

Account list:
  [icon blue]   Wallet      Rp xxx.xxx
  [icon green]  Bank        Rp xxx.xxx
  [icon yellow] Debit       Rp xxx.xxx
```

### Add Account
```
Header (blue): <- "Add account"

Card (overlaps header):
  Rp ___________

  Account name
  ___________

  Account icon
  [wallet blue] [debit yellow] [savings green] [e-wallet purple]
  [other grey]

  [          Add          ]
```

### Account Detail
```
Header (blue): <- "Account"

Card (overlaps header):
  Rp ___________

  Account name
  ___________

  Account icon
  [wallet blue] [debit yellow] [savings green] [e-wallet purple]
  [other grey]

  [  Delete  ]     [   Edit   ]
```

### Transfer
```
Header (blue): <- "Transfer"

Card (overlaps header):
  From
  [Wallet A ▼]

  To
  [Wallet A ▼]

  Transfer Amount
  Rp ___________

  Date                    [calendar icon]
  [DD/M Today] [DD/M Yesterday] [DD/M Last]

  Note (Optional)
  ___________

  [        Transfer        ]
```

### History (Transfer History)
```
Header (blue): <- "History"

Card:
  [Day] [Week] [Month] [Year]
  <- label periode >> ->

  [grey icon]  From → To      Rp XXX.XXX
               Notes
```

### Extras
```
Header (blue, blob pattern):
  "Extras"

Summary card (overlaps header):
  Summary (bold)
  Total subscription per month:    Rp XXX.XXX
  Total wishlist:                  X Item

Section: Upcoming Payments  |  See all
  Horizontal scroll square cards:
    [icon container grey]
    Sub name (bold)
    Rp XXX.XXX/month

Section: Wishlist  |  See all
  [icon container]  Wishlist name (bold)        High/Medium/Low (colored)
                    Rp XXX.XXX from Rp XXX.XXX
                    [progress bar, color = category color]
```

### Wishlist List
```
Header (blue): <- "Wishlist"

Summary card (overlaps header):
  Summary (bold)
  Total Wishlist:    X item
  Total Target:      Rp XXX.XXX

Section label: Wishlist

List:
  [icon container]  Wishlist name (bold)        High/Medium/Low (colored)
                    Rp XXX.XXX from Rp XXX.XXX
                    [progress bar, color = category color]

  [+ Add Wishlist]  (dashed outlined card, grey text, at bottom of list)
```

### Regular Payments List
```
Header (blue): <- "Regular Payments"

Summary card (overlaps header):
  Summary (bold)
  Total payment per month:    Rp XXX.XXX
  Total payment               X Item

Section label: Upcoming payments

List:
  [icon container grey]  My Payment (bold)        Rp XXX.XXX/mo
                         Account | DD Mon YYYY

  [+ Add Regular Payment]  (dashed outlined card, grey text, at bottom of list)
```

## Icons

### Transaction Categories - Expense
```
Food & Drinks   → burger/fast_food
Transport       → directions_car
Shopping        → shopping_bag
Leisure         → music_note
Health          → favorite/heart
Education       → school/graduation cap
Bills           → receipt
Other           → question mark
```

### Transaction Categories - Income
```
Salary          → payments/cash
Freelance       → laptop/monitor
Business        → storefront
Gift            → gift box
Other           → question mark
```

### Regular Payment Categories
```
Streaming       → play_circle
Cicilan         → payments
Utilitas        → bolt
Iuran           → groups
Software        → laptop
Other           → category
```

### Wishlist Categories
```
Electronics     → devices
Fashion         → checkroom
Food            → restaurant
Travel          → flight
Health          → favorite
Other           → category
```

### Account Icons
```
Wallet          → wallet
Debit/Bank      → card/credit_card
Savings         → savings/piggy bank
E-Wallet        → phone_android
Other           → question mark
```

### Navigation Icons
```
Dashboard       → grid_view (4 squares)
Transactions    → bar_chart
[+]             → add (always blue circle, raised)
Accounts        → account_balance_wallet
Extras          → star
```

### Action Icons
```
Transfer history  → swap_horiz / compare_arrows
Add account       → add
Transfer          → swap_horiz / compare_arrows
Filter            → filter_alt (funnel icon)
Calendar          → calendar_month
Back              → arrow_back
```