# Script to download weather icons (PNG format)
$iconsDir = "resources\icons"

# Create icons directory if it doesn't exist
if (-not (Test-Path $iconsDir)) {
    New-Item -ItemType Directory -Path $iconsDir -Force | Out-Null
    Write-Host "Created directory: $iconsDir"
}

# Using Icons from icons8 (free PNG icons)
# Alternative: You can use any other free icon sources
$icons = @{
    "sun.png" = "https://img.icons8.com/color/256/summer--v1.png"
    "partly_cloudy.png" = "https://img.icons8.com/color/256/partly-cloudy-day--v1.png"
    "cloudy.png" = "https://img.icons8.com/color/256/cloud--v1.png"
    "rain.png" = "https://img.icons8.com/color/256/rain--v1.png"
    "storm.png" = "https://img.icons8.com/color/256/storm--v1.png"
    "snow.png" = "https://img.icons8.com/color/256/snow--v1.png"
    "fog.png" = "https://img.icons8.com/color/256/fog-day--v1.png"
    "error.png" = "https://img.icons8.com/color/256/error--v1.png"
    "default.png" = "https://img.icons8.com/color/256/partly-cloudy-day--v2.png"
}

Write-Host "Downloading weather icons from Icons8..."
foreach ($icon in $icons.GetEnumerator()) {
    $outputPath = Join-Path $iconsDir $icon.Key
    try {
        Write-Host "Downloading $($icon.Key)..."
        Invoke-WebRequest -Uri $icon.Value -OutFile $outputPath
        Write-Host "Downloaded: $outputPath"
    } catch {
        Write-Host "Failed to download $($icon.Key): $_"
    }
}

Write-Host "Download complete!"
Write-Host "Note: Icons are from Icons8 (free for personal use)"
Write-Host "For commercial use, please check: https://icons8.com/license"
