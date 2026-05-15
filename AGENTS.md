# DoitDuit - Agent Context

**Project**: DoitDuit - Android expense tracker app

**Tech Stack**: Kotlin, Jetpack Compose, Material 3, MVVM + Clean Architecture, Room, Hilt, Vico

**Minimum SDK**: API 26 (Android 8.0)

**Scope (MVP)**:
- Transaction tracking (income/expense) with categories
- Dashboard (balance, total income, total expenses)
- Donut chart by category per account/period
- Bar chart monthly trend in Transactions screen
- Wishlist (name, target price, priority, account)
- Regular Payment / subscription tracker (name, amount, billing cycle, renewal date, account)
- Multi-account (name, icon, balance)
- Transfer between accounts

**Out of scope for now:** OCR, cloud sync, login, notifications, custom categories, export

## Database Schema
```
accounts: id, name, icon, balance, created_at
transactions: id, account_id, type (income/expense), amount, category_id, date, note, created_at
categories: id, name, icon, color
wishlist_items: id, name, target_price, priority (high/medium/low), status (saved/purchased), account_id, created_at
regular_payments: id, name, amount, billing_cycle (monthly/yearly), next_renewal_date, category_id, account_id, is_active
transfers: id, from_account_id, to_account_id, amount, date, note, created_at
```

## Navigation
Bottom nav bar with 5 tabs:
```
Dashboard | Transactions | [+] | Accounts | Extras
```
- **[+]** center button: add transaction (future: speed dial for OCR)
- **Extras**: contains Wishlist and Regular Payment

## Screens

### Dashboard
- Header: selected account name + balance + dropdown to switch account (default: all accounts)
- Card container: Tab (Expense | Income) > Period filter (Day/Week/Month/Year) > period navigator (<- label >> ->) > Donut chart with total amount in center
- Regular Payment preview: max 3, horizontal scroll card, conditional (hidden if none)
- Latest by category: max 5 categories, shows category name + transaction count + total amount, tap to go to Category Detail screen
- All sections filter by selected account and active period

### Transactions
- Header: "Transactions" + filter icon (opens bottom sheet)
- Period filter: Day | Week | Month | Year + navigator (<- label ->)
- Summary card: Total Income, Total Expense, Net
- Bar chart (income/expense per period)
- Transaction list grouped by:
    - Daily: no grouping
    - Weekly/Monthly: grouped by day
    - Yearly: grouped by month (collapsible)
- Bottom sheet filter: Type (All/Income/Expense), Account (chip selector), Category (chip selector, changes based on type)

### Category Detail
- Header: "Transactions by Category"
- Category icon + name
- Transaction list grouped by day (inherits period and account filter from previous screen)
- Tap item goes to Transaction Detail screen

### Transaction Detail / Edit
- Same layout as Add Transaction screen
- Edit and delete button in header

### Accounts
- Header: "Accounts"
- Total balance (all accounts)
- List of accounts (icon, name, balance)
- Buttons: Transfer, Add Account, Transfer History

### Transfer
- From account, To account, Amount, Date, Note (optional)

### Transfer History
- List: from account > to account, amount, date, note

### Extras
- Header: "Extras"
- Summary card: Total regular payment/month, Total wishlist items
- Regular Payment section: horizontal scroll card, max 3, See all button
- Wishlist section: list, max 3, See all + Add Wishlist button at bottom of list

### Wishlist List
- Header: "Wishlist" + add button
- Summary: total items, total target
- Full list with progress bar, priority, account

### Regular Payment List
- Header: "Regular Payments" + add button
- Total per month
- Full list: icon, name, amount/cycle, account, next renewal date

## Design
See DESIGN.md for complete design guidelines including colors, typography, icons, and UI components.

## Constraints
- Local only, no login
- IDR currency only
- No hardcoded strings (use strings.xml)
- All icons: white (#FFFFFF), container uses category/account color