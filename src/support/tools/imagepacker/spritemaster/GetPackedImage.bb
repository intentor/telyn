AppTitle "SpriteMaster Pro - GetPackedImage()"

Include "..\includes\smPro.bb"

; set up display
Graphics3D 440,280,0,2
SetBuffer BackBuffer()
ClsColor 30,80,140 : Cls

; load pack
pack1=LoadPack("..\packs\testpack.png")

; get 2D image from pack
fish=GetPackedImage("fish",pack1)
runner=GetPackedImage("runner",pack1)
numframes=PackedImageFrames("runner",pack1)

; pack no longer needed
FreePack pack1

frm=1

Repeat
	Cls
	count=count+1
	DrawImage fish,90,80
	If count Mod 8=0 frm=frm+1
	If frm>numframes frm=1
	DrawImage runner,100,180,frm-1
	Text 10,10,"image=GetPackedImage(index/name$,pack)"
	Text 20,40,"2D images extracted from pack."
	Text 20,60,"Using standard DrawImage command to display .."
	Text 20,280-30,"Runner - Frame "+Str(frm)+" of "+Str(numframes)
	Flip
Until KeyHit(1)

; cleanup
FreeImage image
EndGraphics
End