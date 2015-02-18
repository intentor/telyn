AppTitle "SpriteMaster Pro - Pixel accuracy test"

Include "..\includes\smPro.bb"

Const sw=640,sh=480

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2
CameraClsMode spritecamera,False,True
ClsColor 100,100,100 : Cls
SetFont LoadFont("tahoma",17,1)

; load image pack and create single-surface sprites
pack1=LoadPack("..\packs\testpack.png")

Const tilesX=6,tilesY=9

For y=1 To tilesX
	For x=1 To tilesY
		n=n+1
		s.smSprite=GetPackedSprite("testbox",pack1)
		PositionSprite s,-40+x*s\width,-20+y*s\height
	Next
Next

RenderWorld

Text 10,10,"Pixel accuracy test"
Text 10,sh-32,(tilesX*tilesY)+" sprites extracted"

Flip
WaitKey

; cleanup
FreePack pack1
EndSpriteGraphics

End