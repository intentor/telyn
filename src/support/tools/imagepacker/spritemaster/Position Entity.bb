AppTitle"SpriteMasterPro - Positioning an entity"

Include "..\includes\smPro.bb"

; set up sprite display
SpriteGraphics 640,480,0,2 , 1.0001

; reference sprite
sprite.smSprite=CreateSprite(100,18)
HandleSprite sprite
tex=CreateTexture(100,18)
SetBuffer TextureBuffer(tex)
Color 210,210,210
Rect 0,0,100,18,True
Color 10,10,10
Text 2,2,"SpriteMaster"
SpriteTexture sprite,tex
FreeTexture tex
SetBuffer BackBuffer()

; entity for positioning at 2D coords
sphere=CreateSphere()
EntityColor sphere,0,255,0

; some random cubes
For c=1 To 500
	cube=CreateCube()
	PositionEntity cube,Rnd(-150,150),Rnd(-150,150),Rnd(-150,150)
	EntityColor cube,Rand(255),Rand(255),Rand(255)
Next

; light
l=CreateLight()

MoveMouse 170,100


; LOOP START *********************************
Repeat

	mx=MouseX() : my=MouseY()
	PositionSprite sprite,mx,my
	smPositionEntity sphere,mx,my,15
	
	TurnEntity spritecamera,-0.06,0.3,0.07
	MoveEntity spritecamera,0,0,0.1

	RenderWorld :	Flip

Until KeyHit(1)
; LOOP END ***********************************

; cleanup
FreeSprite sprite
EndSpriteGraphics
End

; position entity at 2D coords (with optional z distance)
; (thanks to James Boyd - BlitzSupport)
Function smPositionEntity(ent,x#,y#,z#=10.0 , cam=-1 , camzoom#=1.0)
	If cam<0 cam=spritecamera
	Local gw=GraphicsWidth()
	Local gh=GraphicsHeight()
	Local xp#=-((gw Shr 1)-x)
	Local yp#= (gh Shr 1)-y
	EntityParent ent,cam
	Local div#=(gw/(2/camzoom))/z
	PositionEntity ent,xp/div,yp/div,z
End Function