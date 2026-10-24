Set-Location $PSScriptRoot
New-Item -ItemType Directory -Force -Path "out" | Out-Null
$files = Get-ChildItem -Path . -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -d out $files
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp out Main
