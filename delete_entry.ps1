# Script to find and delete timetable entry for Day Order 1, 08:00-08:50

# Wait for server to start
Start-Sleep -Seconds 5

# Fetch all timetable entries
$entries = Invoke-RestMethod -Uri "http://localhost:8080/api/timetable" -Method Get

# Find the entry for Day Order 1, 08:00-08:50
$targetEntry = $entries | Where-Object { 
    $_.dayOfWeek -eq "1" -and $_.startTime -eq "08:00" -and $_.endTime -eq "08:50" 
}

if ($targetEntry) {
    Write-Host "Found entry to delete:"
    Write-Host "ID: $($targetEntry.id)"
    Write-Host "Course: $($targetEntry.courseName)"
    Write-Host "Faculty: $($targetEntry.faculty)"
    Write-Host "Room: $($targetEntry.roomName)"
    Write-Host "Day: $($targetEntry.dayOfWeek), Time: $($targetEntry.startTime)-$($targetEntry.endTime)"
    
    # Delete the entry
    $deleteUrl = "http://localhost:8080/api/timetable/$($targetEntry.id)"
    try {
        $result = Invoke-RestMethod -Uri $deleteUrl -Method Delete
        Write-Host "`nEntry deleted successfully!" -ForegroundColor Green
        Write-Host $result
    } catch {
        Write-Host "`nError deleting entry: $_" -ForegroundColor Red
    }
} else {
    Write-Host "No entry found for Day Order 1, 08:00-08:50" -ForegroundColor Yellow
    Write-Host "`nAll entries for Day Order 1:"
    $entries | Where-Object { $_.dayOfWeek -eq "1" } | ForEach-Object {
        Write-Host "$($_.startTime)-$($_.endTime): $($_.courseName)"
    }
}
