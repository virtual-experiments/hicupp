#!/bin/bash
set -e
GIF_FILES=($(cd src; find . -name \*.gif))
BMP_FILES=($(cd src; find . -name \*.bmp))
javac -d bin --module-source-path hicupp=src -m hicupp
for F in $GIF_FILES
do
  cp src/$F bin/hicupp/$F
done
for F in $BMP_FILES
do
  cp src/$F bin/hicupp/$F
done

JPACKAGE_FLAGS=
if [[ "$1" = "--sign" ]]
then
  JPACKAGE_FLAGS=--mac-sign
fi

jpackage -p bin -m hicupp/interactivehicupp.ImagesMain --name HicuppForImages --type app-image $JPACKAGE_FLAGS
if [[ "$1" = "--sign" ]]
then
  ditto -c -k --keepParent --sequesterRsrc HicuppForImages.app HicuppForImages-macos-unstapled.zip
  xcrun notarytool submit HicuppForImages-macos-unstapled.zip --keychain-profile APPLE_ID_PASSWORD --wait
  rm HicuppForImages-macos-unstapled.zip
  xcrun stapler staple HicuppForImages.app
fi
ditto -c -k --keepParent --sequesterRsrc HicuppForImages.app HicuppForImages-macos.zip

jpackage -p bin -m hicupp/interactivehicupp.GeneralMain --name HicuppForGeneralPointSets --type app-image $JPACKAGE_FLAGS
if [[ "$1" = "--sign" ]]
then
  ditto -c -k --keepParent --sequesterRsrc HicuppForGeneralPointSets.app HicuppForGeneralPointSets-macos-unstapled.zip
  xcrun notarytool submit HicuppForGeneralPointSets-macos-unstapled.zip --keychain-profile APPLE_ID_PASSWORD --wait
  rm HicuppForGeneralPointSets-macos-unstapled.zip
  xcrun stapler staple HicuppForGeneralPointSets.app
fi
ditto -c -k --keepParent --sequesterRsrc HicuppForGeneralPointSets.app HicuppForGeneralPointSets-macos.zip

jpackage -p bin -m hicupp/hicupp.SequentialHicupp --name SequentialHicupp --type app-image $JPACKAGE_FLAGS
if [[ "$1" = "--sign" ]]
then
  ditto -c -k --keepParent --sequesterRsrc SequentialHicupp.app SequentialHicupp-macos-unstapled.zip
  xcrun notarytool submit SequentialHicupp-macos-unstapled.zip --keychain-profile APPLE_ID_PASSWORD --wait
  rm SequentialHicupp-macos-unstapled.zip
  xcrun stapler staple SequentialHicupp.app
fi
ditto -c -k --keepParent --sequesterRsrc SequentialHicupp.app SequentialHicupp-macos.zip
