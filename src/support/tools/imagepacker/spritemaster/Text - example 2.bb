; this example uses 3 surfaces to control all of the text.
;
; 1 surface to display  'SpriteMaster Pro' and 'Score:'
; 1 surface for the main text you can move around
; 1 surface for the score value which is constantly updating

; TIP:
;
; If you wish to move text around then use Blitz's MoveEntity
; and PositionEntity rather than smPro's PositionSprite
; This should provide a little speed boost.
; See how this example gets the text sprites mesh with
; GetSpriteMesh() and then moves the text sprite around
; with MoveEntity.

AppTitle "SpriteMaster Pro - Text"

Include "..\includes\smPro.bb"

Const sw=800,sh=600

tred%=250
tgreen%=250
tblue%=55
talpha#=1.0

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
ClsColor 80,160,110 : Cls
SetFont LoadFont("Tahoma",18,1)

; load font set and set up 'scorevalue' text sprite
scorevalue.smSprite=CreateTextSprite("..\packs\fontpack.png",1)

; load font set and set up 'default' text sprite
deftext.smSprite=CreateTextSprite("..\packs\fontpack.png",1)
AddText deftext,sw/4*3-130,sh-40,"Score:",50,200,200
AddText deftext,sw/4,08,"SpriteMaster Pro",200,10,205

; load font set and set up 'textsprite' text sprite
textsprite.smSprite=CreateTextSprite("..\packs\fontpack.png",1)
textsprite_entity=GetSpriteMesh(textsprite)
MoveEntity textsprite_entity,44,-sh/2,0


Dim t$(4)
t$(1)="This example shows text"+Chr$(10)
t$(1)=t$(1)+"created with *quad sprites*"+Chr$(10)+Chr$(10)
t$(1)=t$(1)+"Move me with the MOUSE"+Chr$(10)

t$(2)="The text sprite is set up with:"+Chr$(10)
t$(2)=t$(2)+"CreateTextSprite()"

t$(3)="Text is cleared / added with:"+Chr$(10)
t$(3)=t$(3)+"ClearText / AddText"

t$(4)="Press 'C' to change my color.."

AddText textsprite,0,0,t$(1) , tred,tgreen,tblue,talpha

mx#=70 : my#=230 : txt=1
MoveMouse sw/2,sh/2

Repeat
	Cls
	
	count=count+11
	If count Mod 10=0
	score=score+Rand(100)
	; update score
	ClearText scorevalue
	AddText scorevalue,sw/4*3,sh-40,Right$("000000"+Str$(score),6)
	EndIf
	
	If KeyHit(17) ; [W]
		w=Not w
		WireFrame w
	EndIf
	
	If KeyHit(20) ; [T]
		txt=txt+1 : If txt=5 txt=1
		ClearText textsprite
		AddText textsprite,0,0,t$(txt) , tred,tgreen,tblue,talpha
	EndIf
	
	If KeyHit(46) ; [C]
		ClearText textsprite
		tred=Rand(255) : tgreen=Rand(255) : tblue=Rand(255)
		talpha#=Rnd(1.0)
		AddText textsprite,0,0,t$(txt),tred,tgreen,tblue,talpha
	EndIf

	mx#=MouseXSpeed() : my#=MouseYSpeed()
  MoveEntity textsprite_entity,mx,-my,0
	MoveMouse sw/2,sh/2
	
	RenderWorld
	
	Text 10,70,"[T] Change text message"
	Text 10,90,"[C] Change text color"

	Flip
Until KeyHit(1)
	


; cleanup
FreeSprite scorevalue
FreeSprite deftext
FreeSprite textsprite
EndSpriteGraphics

End