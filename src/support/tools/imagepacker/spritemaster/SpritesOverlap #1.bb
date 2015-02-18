AppTitle"SpriteMaster Pro - SpritesOverlap()"

; the include file
Include "..\includes\smPro.bb"

Const sw=800,sh=600

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
MoveMouse sw/4*3,sh/3
f1=LoadFont("courier",18,1)
f2=LoadFont("tahoma",60,1)

; load pack and create single-surface sprites
pack1=LoadPack("..\packs\testpack.png")

; grab floppy from pack
sprite1.smSprite=getPackedSprite("floppy",pack1)
sprite2.smSprite=getPackedSprite("scene",pack1)

; centre the sprites handles/axis
HandleSprite sprite1 : HandleSprite sprite2
positionsprite sprite2,sw*0.3,sh*0.55


; main loop
Repeat

	coll=SpritesOverlap(sprite1,sprite2)
	ClsColor 150,160,100
	If coll ClsColor 200,10,10
	Cls
	
	If KeyHit(17) wf=Not wf : WireFrame wf ; [W]
	If KeyHit(19) ; [R]
		resetsprite sprite1 : resetsprite sprite2
		HandleSprite sprite1 : HandleSprite sprite2
	EndIf
	
	PositionSprite sprite1,MouseX(),MouseY()

	If KeyHit(57) ; [SPACE]
		RotateSprite sprite1,Rand(360)
		RotateSprite sprite2,Rand(360)
		ResizeSprite sprite1,Rand(50,280),Rand(50,280)
		ResizeSprite sprite2,Rand(50,280),Rand(50,280)
		flipsprite sprite1,Rand(0,1),Rand(0,1)
		flipsprite sprite2,Rand(0,1),Rand(0,1)
	EndIf
	
	RenderWorld
		
	Color 255,255,255 :	SetFont f1
	Text sw/2,5,"result=SpritesOverlap(sprite1,sprite2)",1
	Text 20,050,"[Mouse] Positions sprite1"
	Text 20,070,"[SPACE] Randomize size and rotation of sprites"
	Text 20,090,"[R] Reset sprites"
	Text 20,110,"[W] Toggle wireframe mode"
	Color 0,0,0 : SetFont f2
	Text 80,sh-70,"result = "+coll
	
	Plot sw*0.3,sh*0.55 ; where sprite#2 axis/handle is
	
	Flip
Until KeyHit(1)

; cleanup
FreePack pack1
EndSpriteGraphics
End