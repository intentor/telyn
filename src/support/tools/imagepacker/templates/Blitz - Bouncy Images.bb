; Pack Viewer - Generated from Image Packer

%COPYINCLUDEFILE%ImageMaster.bb
Include "ImageMaster.bb"

Const sw=640 , sh=480
Graphics sw,sh
SetBuffer BackBuffer()
ClsColor 60,70,90 : Cls
SetFont LoadFont("tahoma",19)

Type imageType
	Field image
	Field x,y,w,h
	Field dx,dy
	Field frame
	Field numframes
End Type

; load the sprite pack
%GLOB%%PACKNAME%=LoadPack("%PACKFILENAME%")
numImages=CountPackedImages(%PACKNAME%)

; grab all images from the pack
Dim img.ImageType(numImages)
For n=1 To numImages
	img(n)=New imageType
	img(n)\image=GetPackedImage(n,%PACKNAME%)
	img(n)\numframes=PackedImageFrames(n,%PACKNAME%)
	img(n)\w=ImageWidth(img(n)\image)
	img(n)\h=ImageHeight(img(n)\image)
	img(n)\x=Rand(10,sw-img(n)\w-10)
	img(n)\y=Rand(10,sh-img(n)\h-10)
	img(n)\dx=Rand(1,3) : If Rnd(1)<0.5 img(n)\dx=-img(n)\dx
	img(n)\dy=Rand(1,3) : If Rnd(1)<0.5 img(n)\dy=-img(n)\dy
Next

; free up the pack (no longer needed)
FreePack %PACKNAME%

Repeat

	Cls
	count=count+1
	; position all images on screen
	For n=1 To numImages
		If img(n)\x<1 Or img(n)\x>(sw-img(n)\w) img(n)\dx=-img(n)\dx
		If img(n)\y<1 Or img(n)\y>(sh-img(n)\h) img(n)\dy=-img(n)\dy
		img(n)\x=img(n)\x+img(n)\dx
		img(n)\y=img(n)\y+img(n)\dy
		If count Mod 6=0
			img(n)\frame=img(n)\frame+1
			If img(n)\frame>=img(n)\numframes img(n)\frame=0
		EndIf
		DrawImage img(n)\image,img(n)\x,img(n)\y,img(n)\frame
	Next

	Text sw/2,8,"ImageMaster",True
	Text 15,sh-30,"Images in pack: "+Str$(numImages)
	Text sw/2,sh-50,"Hold SPACE to turn off frame sync",1
	
	; FPS timer
	If MilliSecs()>oldTime
	   oldTime=MilliSecs()+1000
	   fps=frameCount : frameCount=0
	Else
	   frameCount=frameCount+1
	EndIf
	Text 10,10,fps+" FPS"
	
	Flip Not KeyDown(57)

Until KeyHit(1)

; cleanup
For n=1 To numImages
	FreeImage img(n)\image
Next
EndGraphics
End