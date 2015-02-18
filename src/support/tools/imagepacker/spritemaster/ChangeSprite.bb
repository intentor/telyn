AppTitle "SpriteMaster Pro - ChangeSprite"

Include "..\includes\smPro.bb"

Const sw=600,sh=300

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2
CameraClsColor spritecamera,60,10,30
SetFont LoadFont("courier",18,1)

; load pack and grab sprite
pack1=LoadPack("..\packs\testpack.png")
sprite.smSprite=GetPackedSprite("runner",pack1)
PositionSprite sprite,sw/2,sh/2
HandleSprite sprite,sprite\width/2,sprite\height

Repeat
	count=count+1

	If KeyHit(57) ; [SPACE]
		whichsprite=whichsprite+1
		Select whichsprite
			Case 1
			ChangeSprite sprite,"rocket" ,52,24,80,26
			Case 2
			ChangeSprite sprite,"explode"
			Case 3
			ChangeSprite sprite,"fish"
			Case 4
			ChangeSprite sprite,"runner"
			whichsprite=0
		End Select
	EndIf

	If KeyHit(45) FlipSprite sprite,1,0 ; [X]
	If KeyHit(21) FlipSprite sprite,0,1 ; [Y]

	If count Mod 6=0
		PositionSprite sprite,sw/2,sh/2,sprite\frame+1
	Else
		PositionSprite sprite,sw/2,sh/2
	EndIf
	
	RotateSprite sprite,-Float(count)/2

	RenderWorld

	Text sw/2,10,"ChangeSprite sprite , index/name$ [,rx,ry,rw,rh]",1
	Text 20,40,"[SPACE] - Change sprites image"
	Text 20,60,"[X] - Flip horizontally"
	Text 20,80,"[Y] - Flip vertically"
	
	Text 30,sh-30,"Sprite = "+sprite\name$

	Flip
	
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics

End