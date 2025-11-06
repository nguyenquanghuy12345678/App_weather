# Convert SVG icons to PNG
# Requires: ImageMagick installed and in PATH
# Download from: https://imagemagick.org/script/download.php

$iconsDir = "resources\icons"
$size = 256

Write-Host "Converting SVG to PNG icons..."
Write-Host "Icon size: ${size}x${size} pixels"
Write-Host ""

# Check if ImageMagick is installed
try {
    $magickVersion = magick -version 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "ImageMagick not found"
    }
} catch {
    Write-Host "ERROR: ImageMagick is not installed or not in PATH"
    Write-Host "Please install ImageMagick from: https://imagemagick.org/script/download.php"
    Write-Host ""
    Write-Host "Alternative: Use online converter to convert SVG to PNG:"
    Write-Host "1. Go to: https://cloudconvert.com/svg-to-png"
    Write-Host "2. Upload each SVG file from resources\icons\"
    Write-Host "3. Download PNG files and replace the SVG files"
    exit 1
}

# Get all SVG files
$svgFiles = Get-ChildItem -Path $iconsDir -Filter "*.svg"

if ($svgFiles.Count -eq 0) {
    Write-Host "No SVG files found in $iconsDir"
    Write-Host "PNG conversion not needed - icons may already be in PNG format"
    exit 0
}

foreach ($svgFile in $svgFiles) {
    $pngFile = $svgFile.FullName -replace '\.svg$', '.png'
    Write-Host "Converting: $($svgFile.Name) -> $([System.IO.Path]::GetFileName($pngFile))"
    
    try {
        # Convert SVG to PNG with transparent background
        magick convert -background none -size "${size}x${size}" $svgFile.FullName $pngFile
        
        # Remove SVG file after successful conversion
        Remove-Item $svgFile.FullName
        Write-Host "  Success!" -ForegroundColor Green
    } catch {
        Write-Host "  Failed: $_" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Conversion complete!"
Write-Host "PNG icons saved in: $iconsDir"
