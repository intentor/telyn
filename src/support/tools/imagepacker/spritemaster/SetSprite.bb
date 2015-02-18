AppTitle "SpriteMaster Pro - SetSprite"

Include "..\includes\smPro.bb"

; set up sprite display
SpriteGraphics 640,300,0,2
CameraClsMode spritecamera,False,True
ClsColor 100,100,100
Cls

pack1=LoadPack("..\packs\testpack.png")    ; load pack
sprite.smSprite=GetPackedSprite("runner",pack1) ; extract sprite

; set multiple sprite parameters (pos/frame/size/rotation/color/alpha)
SetSprite sprite, 12 ,370,60,140,180 , -4 , 55,240,20 , 0.6

RenderWorld

Text 5,5,"SetSprite sprite,frame,x,y[,w][,h][,rotation][,red][,green][,blue][,alpha]"
Text 10,030,"Example sprite has been set to the following:"
Color 200,200,200
Text 30,060,"Frame="+sprite\frame
Text 30,080,"Position X="+Int(sprite\x)
Text 30,100,"Position Y="+Int(sprite\y)
Text 30,120,"Width="+Int(sprite\width)
Text 30,140,"Height="+Int(sprite\height)
Text 30,160,"Rotation="+Int(sprite\angle)
Text 30,180,"Color=   R:"+sprite\red+"  G:"+sprite\green+"  B:"+sprite\blue
Text 30,200,"Alpha="+sprite\alpha

Flip : WaitKey

FreePack pack1     ; free up pack and extracted sprites
EndSpriteGraphics  ; close down sprite display
End