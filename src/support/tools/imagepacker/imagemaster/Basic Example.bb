; ImageMaster - Basic example

Include "..\includes\ImageMaster.bb"

Const sw=320 , sh=240

Graphics sw,sh,0,2 ; set up sprite display
SetBuffer BackBuffer()

pack1=LoadPack("..\packs\testpack.png") ; load pack
image=GetPackedImage("fish",pack1) ; extract image


For d=1 To 50
	DrawImage image,Rand(sw)-80,Rand(sh)-40
Next

Flip : WaitKey

FreePack pack1 ; free up the pack
EndGraphics ; close down display
End