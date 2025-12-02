# Client-Server Database Synchronization - Test Checklist

## Quick Test (Single Machine - Localhost)

### Setup
- [x] Code compiled successfully
- [ ] Server running on port 8889
- [ ] Client 1 connected
- [ ] Client 2 connected

### Test 1: Add Favorite from Client 1
- [ ] Login as "user1" on Client 1
- [ ] Search for location "Hanoi"
- [ ] Click "Add to Favorites" button
- [ ] Check server console for log: "Favorite added by user1: Hanoi"
- [ ] Verify server's weather.db:
  ```batch
  sqlite3 weather.db "SELECT * FROM favorites;"
  ```
  Expected: Row with Hanoi, latitude, longitude

### Test 2: Verify Client 2 Sees Favorite
- [ ] Login as "user2" on Client 2
- [ ] Check favorites list (should automatically load on connection)
- [ ] Verify "Hanoi" appears in favorites list

### Test 3: Add Community Report from Client 1
- [ ] On Client 1, search for "Hanoi"
- [ ] Open Community Reports panel
- [ ] Submit report:
  - Accuracy: 5 stars
  - Comment: "Very accurate weather data!"
- [ ] Check server console for log: "Report added by user1: Hanoi"
- [ ] Verify server's weather.db:
  ```batch
  sqlite3 weather.db "SELECT * FROM community_reports;"
  ```
  Expected: Row with Hanoi, 5, "Very accurate weather data!", user1

### Test 4: Verify Client 2 Sees Report
- [ ] On Client 2, search for "Hanoi"
- [ ] Open Community Reports panel
- [ ] Click refresh/view reports button
- [ ] Verify user1's report appears with 5 stars and comment

### Test 5: Remove Favorite from Client 2
- [ ] On Client 2, click "Remove from Favorites" for Hanoi
- [ ] Check server console for log: "Favorite removed by user2: Hanoi"
- [ ] Verify server's weather.db:
  ```batch
  sqlite3 weather.db "SELECT * FROM favorites;"
  ```
  Expected: Hanoi row should be deleted

### Test 6: Verify Client 1 Sees Removal
- [ ] On Client 1, refresh or restart client
- [ ] Check favorites list
- [ ] Verify "Hanoi" no longer appears

### Test 7: Connection Sync on Fresh Login
- [ ] Close Client 1
- [ ] On Client 2, add favorite "Ho Chi Minh City"
- [ ] On Client 2, add report for "Ho Chi Minh City" (3 stars, "Good data")
- [ ] Start Client 1 again, login as "user1"
- [ ] Verify both favorites and reports loaded automatically:
  - Ho Chi Minh City in favorites
  - Report from user2 visible

## Advanced Test (Multi-Machine)

### Network Setup
- [ ] Server Machine IP: ___________________
- [ ] Client Machine can ping Server: `ping <server-ip>`
- [ ] Firewall allows port 8889 on Server Machine
- [ ] run_client.bat updated with correct SERVER_HOST

### Test 8: Multi-Machine Favorite Sync
- [ ] Server running on Machine 1
- [ ] Client running on Machine 2
- [ ] Add favorite "Da Nang" from Machine 2
- [ ] Check weather.db on Machine 1 (server)
- [ ] Verify "Da Nang" appears in server database

### Test 9: Multi-Machine Report Sync
- [ ] From Machine 2, add report for "Da Nang"
- [ ] Check weather.db on Machine 1 (server)
- [ ] Verify report appears in server database
- [ ] Start another client on Machine 1 or Machine 3
- [ ] Verify report is visible

## Database Verification Commands

### Check Favorites
```batch
sqlite3 weather.db
sqlite> SELECT * FROM favorites ORDER BY added_at DESC;
sqlite> .quit
```

### Check Reports
```batch
sqlite3 weather.db
sqlite> SELECT location, accuracy, comment, username, timestamp FROM community_reports ORDER BY timestamp DESC;
sqlite> .quit
```

### Count Records
```batch
sqlite3 weather.db
sqlite> SELECT COUNT(*) as favorite_count FROM favorites;
sqlite> SELECT COUNT(*) as report_count FROM community_reports;
sqlite> .quit
```

## Expected Behavior Summary

| Action | Client Behavior | Server Behavior | Database Update |
|--------|----------------|-----------------|-----------------|
| Add Favorite | Send MSG_ADD_FAVORITE | INSERT OR REPLACE into favorites | Immediate |
| Remove Favorite | Send MSG_REMOVE_FAVORITE | DELETE from favorites | Immediate |
| Get Favorites | Send MSG_GET_FAVORITES | SELECT all favorites | Read-only |
| Add Report | Send MSG_ADD_REPORT | INSERT into community_reports | Immediate |
| Get Reports | Send MSG_GET_REPORTS | SELECT reports (filtered) | Read-only |
| Login Success | Auto-request favorites | Send favorites list | Read-only |

## Troubleshooting

### Issue: Favorites not syncing
1. Check server logs for "Favorite added by..." messages
2. Verify client console shows no exceptions
3. Check server's weather.db with sqlite3
4. Verify client sent MSG_ADD_FAVORITE (use server logs)

### Issue: Reports not appearing
1. Verify client calls requestReports() or clicks refresh
2. Check server logs for "Reports list sent to..." messages
3. Verify server's weather.db has records
4. Check if location filter is correct (case-sensitive)

### Issue: Empty lists on connection
1. Normal if database is empty (first time)
2. Add test data manually to verify sync works
3. Check server returns MSG_SUCCESS with empty list (valid)

### Issue: Client uses local database instead of server
1. This should NOT happen with new code
2. Verify client code uses ServerCallback, not DBManager
3. Check SearchHistoryManager and CommunityReportsManager use callbacks

## Success Criteria
- ✅ All clients see same favorites
- ✅ All clients see same community reports
- ✅ Server's weather.db is single source of truth
- ✅ Client local databases NOT used for favorites/reports
- ✅ Data persists across client disconnects/reconnects
- ✅ Multi-machine setup works correctly
