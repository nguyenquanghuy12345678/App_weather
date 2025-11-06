# Download UI icons
$iconsDir = "resources\icons"

# Create icons directory if it doesn't exist
if (-not (Test-Path $iconsDir)) {
    New-Item -ItemType Directory -Path $iconsDir -Force | Out-Null
}

# UI Icons from Icons8
$icons = @{
    "search.png" = "https://img.icons8.com/color/256/search--v1.png"
    "humidity.png" = "https://img.icons8.com/color/256/water.png"
    "wind.png" = "https://img.icons8.com/color/256/wind--v1.png"
    "refresh.png" = "https://img.icons8.com/color/256/refresh--v1.png"
    "disconnect.png" = "https://img.icons8.com/color/256/cancel--v1.png"
    "user.png" = "https://img.icons8.com/color/256/user--v1.png"
    "connect.png" = "https://img.icons8.com/color/256/connect--v1.png"
    "cloud_app.png" = "https://img.icons8.com/color/256/cloud--v2.png"
    "server.png" = "https://img.icons8.com/color/256/server--v1.png"
}

Write-Host "Downloading UI icons from Icons8..."
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
