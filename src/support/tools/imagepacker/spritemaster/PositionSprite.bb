AppTitle "SpriteMaster Pro - PositionSprite"

Include "..\includes\smPro.bb"

Const sw=800,sh=600

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
ClsColor 50,60,70
MoveMouse sw/2,sh/2
SetFont LoadFont("courier",18,1)

; load pack and create single-surface sprites
pack1=LoadPack("..\packs\testpack.png")
numsprites=CountPackedImages(pack1)

; load reference 2D image
img=LoadImage("..\packs\testpack.png")

; get all sprites from pack
For num=1 To numsprites
	sprite.smSprite=GetPackedSprite(num,pack1)
	HandleSprite sprite
Next

Repeat
	Cls
	If showpack	DrawBlock img,40,130
	
	If KeyHit(17) wf=Not wf : WireFrame wf ; [W]
	
	x=MouseX() : y=MouseY()

	If KeyHit(57) showpack=Not showpack ; [SPACE]

	If MouseHit(1) ; [Left Mouse Button]
		sprite=After sprite
		If sprite=Null sprite=First smSprite
	EndIf

	If MouseHit(2) ; [Right Mouse Button]
		sprite=Before sprite
		If sprite=Null sprite=Last smSprite
	EndIf

	count=count+1
	If count Mod 10=0 change=1 Else change=0
	PositionSprite sprite,x,y,sprite\frame+change
	RenderWorld
		
	Color 255,255,255
	Text sw/2,5,"PositionSprite sprite,x,y[,frame]",1
	Text 20,50,"[Mouse] positions the sprite"
	Text 20,70,"[LMB] / [RMB] Switch to next/previous sprite in pack"
	Text 20,90,"[SPACE] show/hide loaded pack (as 2D image)"
	Color 100,250,100
	num=Handle(sprite)
	Text 20,400,"Sprite "+Str(num-1)+" of "+Str(numsprites)
	Text 20,440,"    Name: "+sprite\name$
	Text 20,460,"Position: "+Int(sprite\x)+","+Int(sprite\y)
	Text 20,480,"    Size: "+Int(sprite\width)+"x"+Int(sprite\height)
	Text 20,510,"   Frame "+sprite\frame+ " of "+sprite\numframes
	
	Flip
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics

End