; SpriteMaster Pro - CopySprite

; the include file
Include "..\includes\smPro.bb"

Const sw=800,sh=600

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
ClsColor 80,60,90

SetFont LoadFont("Tahoma",18,1)

; load pack
pack1=LoadPack("..\packs\testpack.png")
numsprites=CountPackedImages(pack1)

; get all sprites from pack and store in sprite arrays()
For num=1 To numsprites
	sprite.smSprite=GetPackedSprite(num,pack1)
	HandleSprite sprite
Next

num=1
Global copy.smSprite

; loop
Repeat
	Cls
	If showpack	DrawBlock img,40,130
	
	If KeyHit(17) wf=Not wf : WireFrame wf ; [W]
	x=MouseX() : y=MouseY()

	If KeyHit(46) ; [C]
		If copy<>Null FreeSprite copy
		copy=CopySprite(sprite)
		PositionSprite copy,120,260,copy\frame
		For hs.smSprite=Each smSprite
			If hs=sprite Exit
		Next
	EndIf
	
	If KeyHit(19) ; [R]
		ResetSprite sprite
		HandleSprite sprite
	EndIf
	
	If KeyHit(57) ; [SPACE]
		SpriteColor sprite,Rand(255),Rand(255),Rand(255),Rnd(1)
		ResizeSprite sprite,Rand(50,100),Rand(50,100)
		RotateSprite sprite,Rand(360)
	EndIf
	
	If MouseHit(1) ; [LMB]
		sprite=After sprite
		If sprite=Null sprite=First smSprite
		If sprite=copy sprite=After sprite
		If sprite=Null sprite=First smSprite
	EndIf
	
	If MouseHit(2) ; [Right Mouse Button]
		sprite=Before sprite
		If sprite=Null sprite=Last smSprite
		If sprite=copy sprite=Before sprite
	EndIf

	count=count+1
	If count Mod 6=0 fr=fr+1
	PositionSprite sprite,x,y,fr
	
	RenderWorld
		
	Color 255,255,255
	Plot 120,260
	
	Text sw/2,5,"newsprite=CopySprite(sprite)",1
	Text 20,50,"[Mouse] positions the sprite"
	Text 20,70,"[LMB] / [RMB] Switch to next/previous sprite in pack"
	Text 20,90,"[SPACE] Randomize size/rotation/color/alpha"
	Text 20,110,"[R] Reset sprite"
	Text 20,140,"[C] copy current sprite"
	Color 100,250,100
	Text 20,sh-55,"Sprite "+num+" of "+numsprites+"           Name: "+sprite\name$
	Text 20,sh-35,"Position:   X="+x+"  y="+y
	Text 290,sh-35,"Size: "+sprite\width+"x"+sprite\height
	Text 490,sh-35,"Axis: "+sprite\handleX+","+sprite\handleY	
	Flip
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics

End