#!/bin/bash
set -e
GIF_FILES=($(cd src; find . -name \*.gif))
javac -d bin --module-source-path hicupp=src -m hicupp
for F in $GIF_FILES
do
  cp src/$F bin/hicupp/$F
done

jpackage -p bin -m hicupp/interactivehicupp.ImagesMain --name HicuppForImages --type app-image --mac-sign
ditto -c -k --keepParent --sequesterRsrc HicuppForImages-macos-unstapled.zip HicuppForImages.app
xcrun notarytool submit HicuppForImages-macos-unstapled.zip --keychain-profile APPLE_ID_PASSWORD --wait
rm HicuppForImages-macos-unstapled.zip
xcrun stapler staple HicuppForImages.app
ditto -c -k --keepParent --sequesterRsrc HicuppForImages-macos.zip HicuppForImages.app

jpackage -p bin -m hicupp/interactivehicupp.GeneralMain --name HicuppForGeneralPointSets --type app-image --mac-sign
ditto -c -k --keepParent --sequesterRsrc HicuppForGeneralPointSets-macos-unstapled.zip HicuppForGeneralPointSets.app
xcrun notarytool submit HicuppForGeneralPointSets-macos-unstapled.zip --keychain-profile APPLE_ID_PASSWORD --wait
rm HicuppForGeneralPointSets-macos-unstapled.zip
xcrun stapler staple HicuppForGeneralPointSets.app
ditto -c -k --keepParent --sequesterRsrc HicuppForGeneralPointSets-macos.zip HicuppForGeneralPointSets.app

jpackage -p bin -m hicupp/hicupp.SequentialHicupp --name SequentialHicupp --type app-image --mac-sign
ditto -c -k --keepParent --sequesterRsrc SequentialHicupp-macos-unstapled.zip SequentialHicupp.app
xcrun notarytool submit SequentialHicupp--macos-unstapled.zip --keychain-profile APPLE_ID_PASSWORD --wait
rm SequentialHicupp-macos-unstapled.zip
xcrun stapler staple SequentialHicupo.app
ditto -c -k --keepParent --sequesterRsrc SequentialHicupp-macos.zip SequentialHicupp.app
