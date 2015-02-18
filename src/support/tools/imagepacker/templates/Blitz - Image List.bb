; Pack Viewer - Generated from ImagePacker

%COPYINCLUDEFILE%ImageMaster.bb
Include "ImageMaster.bb"

Const sw=640 , sh=480
Graphics sw,sh
SetBuffer BackBuffer()
ClsColor 60,70,80 : Cls

; load the image pack
%GLOB%%PACKNAME%=LoadPack("%PACKFILENAME%")
%GLOB%numImages=CountPackedImages(%PACKNAME%)

; get all images from pack
%ALL%
%GLOB%%IMAGENAME%=GetPackedImage("%IMAGENAME%",%PACKNAME%)

; draw all images to screen
%ALL%
DrawImage %IMAGENAME%,Rand(5,sw-50),Rand(5,sh-50)

Text sw/2,8,"ImageMaster",True
Text 15,sh-30,"Images in pack: "+Str$(numImages)

Flip : WaitKey

; cleanup
FreePack %PACKNAME%
%ALL%
FreeImage %IMAGENAME%
EndGraphics
End