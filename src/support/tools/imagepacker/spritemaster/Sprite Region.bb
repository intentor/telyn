AppTitle "SpriteMaster Pro - Region"

Include "..\includes\smPro.bb"

SpriteGraphics 260,240,0,2             ; set up sprite display
pack1=LoadPack("..\packs\testpack.png")   ; load pack

; get the 'floppy' image
sprite1.smSprite=GetPackedSprite("floppy",pack1)
PositionSprite sprite1,40,60
SpriteAlpha sprite1,0.4

; get a section/region of the 'floppy' image
sprite2.smSprite=GetPackedSprite("floppy",pack1 , 26,10,104,62)
PositionSprite sprite2,90,90
RotateSprite sprite2,8

RenderWorld

Text 10,10,"GetPackedSprite()"
Text 10,30,"- optional 'region' parameters"

Flip
WaitKey

FreePack pack1     ; free up pack and extracted sprites
EndSpriteGraphics        ; close down sprite display
End