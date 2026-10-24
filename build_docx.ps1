$root = 'C:\Users\Sahit Bytyqi\Desktop\klinika-dentare'
$build = Join-Path $root 'word_build'
$docxPath = Join-Path $root 'Detyra-Java2.docx'

if (Test-Path $build) { Remove-Item $build -Recurse -Force }
if (Test-Path $docxPath) { Remove-Item $docxPath -Force }
New-Item -ItemType Directory -Path (Join-Path $build 'word\_rels') -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $build 'docProps') -Force | Out-Null

$ContentTypes = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>
'@
Set-Content -Path (Join-Path $build '[Content_Types].xml') -Value $ContentTypes -Encoding UTF8

$RootRels = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>
'@
New-Item -ItemType Directory -Path (Join-Path $build '_rels') -Force | Out-Null
Set-Content -Path (Join-Path $build '_rels\.rels') -Value $RootRels -Encoding UTF8

$coreXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dcmitype="http://purl.org/dc/dcmitype/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>Detyra Projekti — Klinika Dentare</dc:title>
  <dc:creator>Sahit Bytyqi, Donit Qollaku, Drilon Sekiraqa</dc:creator>
  <cp:lastModifiedBy>Sahit Bytyqi, Donit Qollaku, Drilon Sekiraqa</cp:lastModifiedBy>
  <dcterms:created xsi:type="dcterms:W3CDTF">2026-10-07T00:00:00Z</dcterms:created>
  <dcterms:modified xsi:type="dcterms:W3CDTF">2026-10-07T00:00:00Z</dcterms:modified>
</cp:coreProperties>
'@
Set-Content -Path (Join-Path $build 'docProps\core.xml') -Value $coreXml -Encoding UTF8

$appXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties" xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>Microsoft Office Word</Application>
</Properties>
'@
Set-Content -Path (Join-Path $build 'docProps\app.xml') -Value $appXml -Encoding UTF8

$documentXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    <w:p><w:r><w:rPr><w:b/><w:sz w:val="30"/></w:rPr><w:t>Kolegji AAB</w:t></w:r></w:p>
    <w:p><w:r><w:t>Programim dhe Multimedia</w:t></w:r></w:p>
    <w:p><w:r><w:t>Semestri III</w:t></w:r></w:p>
    <w:p><w:r><w:t>Advanced Programming OOP</w:t></w:r></w:p>
    <w:p><w:r><w:rPr><w:b/><w:sz w:val="28"/></w:rPr><w:t>Detyra projektuese</w:t></w:r></w:p>
    <w:p><w:r><w:t>Klinika dentare</w:t></w:r></w:p>
    <w:p><w:r><w:t>Tema ose moduli i zgjedhur</w:t></w:r></w:p>
    <w:p><w:r><w:t>Moduli: Shërbimet e Klinikës Dentare</w:t></w:r></w:p>
    <w:p><w:r><w:t>Emri dhe mbiemri i studentit: Sahit Bytyqi, Donit Qollaku, Drilon Sekiraqa</w:t></w:r></w:p>
    <w:p><w:r><w:t>Grupi: Java 2</w:t></w:r></w:p>
    <w:p><w:r><w:t>Profesora: Teuta Sahatqija</w:t></w:r></w:p>
    <w:p><w:r><w:t>Viti akademik: 2026/2027</w:t></w:r></w:p>
    <w:p><w:r><w:t>Data e dorëzimit: 07/10/2026</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Faza 1 – Titulli dhe përshkrimi i projektit</w:t></w:r></w:p>
    <w:p><w:r><w:t>Projektin e kemi zhvilluar për menaxhimin e shërbimeve dhe rezervimeve në një klinikë dentare. Programi lejon administratorin dhe përdoruesit e sistemit të shtojnë, editojnë, shohin dhe rezervojnë shërbime dentare me një interfejs grafik të thjeshtë në Java Swing.</w:t></w:r></w:p>
    <w:p><w:r><w:t>Qëllimi i aplikacionit është të lehtësojë punën e recepsionistit dhe administratorit, duke ruajtur të dhënat në skedarë JSON dhe duke ofruar një sistem të organizuar për regjistrimin e pacientëve, shërbimeve dhe rezervimeve.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Faza 2 – Përshkrimi i problemit dhe skenari</w:t></w:r></w:p>
    <w:p><w:r><w:t>Problemi që synon të zgjidhet është menaxhimi i shërbimeve dentare dhe ruajtja e rezervimeve pa mbivendosje, pa humbje të dhënash dhe pa nevojë për aplikim manual. Në një klinikë dentare, çdo shërbim ka një emër, kohëzgjatje, çmim dhe përshkrim.</w:t></w:r></w:p>
    <w:p><w:r><w:t>Një përdorues hyn në sistem, zgjidh shërbimin, vendos datën e rezervimit dhe programi kontrollon nëse data është e vlefshme. Nëse të dhënat janë të sakta, rezervimi regjistrohet në sistem dhe mund të shfaqet në faturë me totalin përkatës.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Faza 3 – Rregullat e biznesit</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Një përdorues duhet të ketë username dhe password të paktën 4 karaktere.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Username-i duhet të jetë unik.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Data e rezervimit nuk mund të jetë para datës së sotme.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Çmimi dhe kohëzgjatja e shërbimit duhet të jenë të vlefshme dhe pozitive.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Shërbimet administrot mund t’i shtojnë, modifikojnë ose fshijnë, ndërsa përdoruesit mund të shohin vetëm listën e shërbimeve dhe të krijojnë rezervime.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Pasi krijohet një rezervim, sistemi ruan numrin e rezervimit, datën, shërbimin, sasinë dhe totalin.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Faza 4 – Kodi në Java</w:t></w:r></w:p>
    <w:p><w:r><w:t>Implementimi është i organizuar në shtresa logjike: model, service dhe ui. Klasa kryesore janë:</w:t></w:r></w:p>
    <w:p><w:r><w:t>• User – përfaqëson përdoruesin dhe rolet e tij.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Sherbimi – përmban emrin, përshkrimin, kohëzgjatjen dhe çmimin e shërbimit.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• Rezervimi – përfaqëson një rresht rezervimi me datë, shërbim, sasi dhe total.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• AuthService – menaxhon login, regjistrim dhe kontrollin e përdoruesve.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• SherbimService – menaxhon shërbimet e klinikës dhe validimin e të dhënave.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• RezervimService – kontrollon datën, krijon rezervimet dhe gjeneron identifikuesin e rezervimit.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• ClinicSwingUI – ndërfaqja grafike e përdoruesit, e ndërtuar me Java Swing.</w:t></w:r></w:p>
    <w:p><w:r><w:t>• JsonStorage – menaxhon ruajtjen e të dhënave në skedarë JSON.</w:t></w:r></w:p>
    <w:p><w:r><w:t>Programi është zhvilluar me një model të thjeshtë, por të qartë, ku logjika e biznesit është e ndarë nga ndërfaqja dhe ruajtja e të dhënave. Kjo e bën aplikacionin të lehtë për të testuar, zgjeruar dhe modifikuar.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Faza 5 – Testimi dhe rastet ekstreme</w:t></w:r></w:p>
    <w:p><w:r><w:t>1. Login i saktë me username dhe password të duhur → programi hyn në sistem.</w:t></w:r></w:p>
    <w:p><w:r><w:t>2. Login me password gabim → sistemi refuzon hyrjen dhe shfaq mesazh.</w:t></w:r></w:p>
    <w:p><w:r><w:t>3. Shtim i shërbimi me çmim dhe kohëzgjatje negative → sistemi refuzon për shkak të vlerave të pavlefshme.</w:t></w:r></w:p>
    <w:p><w:r><w:t>4. Rezervim me datë të kaluar → sistemi e refuzon sepse data nuk plotëson rregullin e biznesit.</w:t></w:r></w:p>
    <w:p><w:r><w:t>5. Krijim i rezervimit me të dhëna të sakta → sistemi e regjistron dhe e ruan në listën e rezervimeve.</w:t></w:r></w:p>
    <w:p><w:r><w:t>6. Test i kufirit me username ekzistues → sistemi nuk lejon regjistrimin e dyfishtë.</w:t></w:r></w:p>
    <w:p><w:r><w:t>Rezultati i testimit tregon se aplikacioni funksionon në situata normale, por edhe në rastet e gabimeve, duke dhënë mesazhe të qarta për përdoruesin.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Faza 6 – Analiza e projektit dhe arsyetimi</w:t></w:r></w:p>
    <w:p><w:r><w:t>Ne zgjedhëm këtë zgjidhje sepse ajo plotëson kërkesat e modulit të klinikës dentare dhe ofron një aplikacion praktik, me përdorim të thjeshtë dhe strukturë të qartë. Klasat janë të ndara sipas përgjegjësisë, ndërsa logjika e biznesit është e izoluar nga ndërfaqja.</w:t></w:r></w:p>
    <w:p><w:r><w:t>Vendimet e dizajnit janë të lidhura me problemin real të klinikës: regjistrimi i shërbimeve, kontrolli i rezervimeve dhe menaxhimi i të dhënave. Alternative të tjera si përdorimi i një baze të dhënash ose një aplikacioni web ishin të mundshme, por kjo zgjidhje është më e lehtë për t’u realizuar, e më e përshtatshme për një projekt Java OOP të semestrit.</w:t></w:r></w:p>
    <w:p><w:r><w:t>Kufizimi kryesor i aplikacionit është se të dhënat ruhen në JSON dhe jo në një bazë të dhënash, prandaj skala dhe performanca e saj janë të kufizuara. Megjithatë, për një sistem të vogël klinik, kjo është një zgjidhje e mjaftueshme dhe e lehtë për tu mbajtur.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Faza 7 – Përfundimi</w:t></w:r></w:p>
    <w:p><w:r><w:t>Me këtë projekt kemi ndërtuar një aplikacion të plotë për menaxhimin e shërbimeve dentare dhe rezervimeve. Gjatë punës, kemi aplikuar koncepte të programimit orientuar në objekte, kontrollin e exception-ave, ruajtjen e të dhënave dhe përdorimin e Java Swing për ndërfaqen grafike.</w:t></w:r></w:p>
    <w:p><w:r><w:t>Ne kemi mësuar se një zgjidhje e mirë nuk është vetëm e lehtë për t’u përdorur, por edhe e organizuar mirë, e testueshme dhe e përshtatshme për rregullat e biznesit. Në një version të ardhshëm, aplikacioni mund të zgjerohet me shtimin e notave të pacientëve, historisë së trajtimeve, ose menaxhimit të stokut dhe materialeve.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Përdorimi i AI si asistent</w:t></w:r></w:p>
    <w:p><w:r><w:t>Ne përdorëm AI si asistent për të analizuar strukturën e kodit, për të kontrolluar logjikën e aplikacionit dhe për të identifikuar gabime të mundshme. Ai na ndihmoi në organizimin e idesë dhe në përmirësimin e dokumentimit, por çdo zgjidhje u verifikua manualisht dhe u përshtat me projektin tonë.</w:t></w:r></w:p>

    <w:p><w:r><w:rPr><w:b/><w:sz w:val="24"/></w:rPr><w:t>Dorëzimi dhe mbrojtja</w:t></w:r></w:p>
    <w:p><w:r><w:t>Raporti është dorëzuar në format Word dhe kodi burimor është i gatshëm për demonstrim. Prezantimi do të përfshijë funksionalitetin kryesor të programit, menaxhimin e rezervimeve dhe shërbimeve, si dhe shpjegimin e strukturës së klasave.</w:t></w:r></w:p>

    <w:sectPr>
      <w:pgSz w:w="12240" w:h="15840"/>
      <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440" w:header="708" w:footer="708" w:gutter="0"/>
    </w:sectPr>
  </w:body>
</w:document>
'@
Set-Content -Path (Join-Path $build 'word\document.xml') -Value $documentXml -Encoding UTF8

$stylesXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal">
    <w:name w:val="Normal"/>
  </w:style>
</w:styles>
'@
Set-Content -Path (Join-Path $build 'word\styles.xml') -Value $stylesXml -Encoding UTF8

$documentRels = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>
'@
New-Item -ItemType Directory -Path (Join-Path $build 'word\_rels') -Force | Out-Null
Set-Content -Path (Join-Path $build 'word\_rels\document.xml.rels') -Value $documentRels -Encoding UTF8

Get-ChildItem $build -Force | Compress-Archive -DestinationPath $docxPath -CompressionLevel Optimal -Force
Remove-Item $build -Recurse -Force
Write-Output "Created $docxPath"
