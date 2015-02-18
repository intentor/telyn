; Pack Viewer - Generated from ImagePacker

Include "..\includes\ImageMaster.bb"

Const sw=340 , sh=280
Graphics sw,sh,0,2
SetBuffer BackBuffer()
ClsColor 60,70,90 : Cls

; load the image pack
pack1=LoadPack("..\packs\testpack.png")
numImages=CountPackedImages(pack1)

; get image from pack
runner=GetPackedImage("runner",pack1)
numframes=PackedImageFrames("runner",pack1)

; free the pack but keep any extracted images
FreePack pack1

frame=1

; main loop
Repeat
	Cls
	count=count+1
	If count Mod 6=0
		frame=frame+1
		If frame=numframes+1 frame=1
	EndIf
	DrawImage runner,120,120,frame-1
	Text 10,10,"ImageMaster - Animation test"
	Text 20,40,"grabbed 'runner' from pack .."
	Text 10,sh-40,"Frame "+frame+" of "+numframes
	Flip
Until KeyHit(1)

; cleanup
EndGraphics
End