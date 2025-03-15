<h1 align="center">商品真实化的Roguelike游戏系统设计与开发</h1>

## 一、概述

### 游戏设计

- 地图随机生成
- 打怪获取属性增强和金钱，怪物随时间增强（难度增强），成功将战利品带出世界则拥有该战利品。

### 后端设计

#### 安全

- JWT

#### 数据表

- 用户（密码加密）
- 商品列表（描述、价格、库存、图片、分类、促销）
- 订单（创建、状态更新）
- 商品用户评分

## 二、要点

- 真实商品属性建模（SKU、价格弹性、生命周期理论），设计商品属性（成本、质量、稀缺性）动态变化的规则

- 用户画像

- 设计**动态定价算法**：玩家需根据实时供需数据（游戏内仪表盘）调整价格策略，体现收益管理理论。
* 模拟**市场竞争机制**：NPC对手使用差异化策略（低价倾销、精准营销），玩家需通过SWOT分析制定应对方案。

## 三、游戏开发

### （一）项目创建

#### 1. 新建游戏项目

创建一个目标平台为桌面的蓝图游戏项目。

![ ](images/游戏开发/a项目创建/1-1.png)

#### 2. 项目配置

在内容目录下创建Blueprints目录，在Blueprints目录下创建游戏模式，命名为BP_GameMode。

![ ](images/游戏开发/a项目创建/2-1.png)

在Maps目录下新建一个新的关卡Main。

![ ](images/游戏开发/a项目创建/2-2.png)

在顶部菜单栏打开编辑 > 项目设置，在项目 > 地图和模式中，将默认模式改为BP_GameMode，将编辑器开始地图和游戏默认地图改为Main。

![ ](images/游戏开发/a项目创建/2-3.png)

将平台 > Windows > 默认RHI改为DirectX 11（该操作可解决Video Memory Has Been exhausted的异常）。

![ ](images/游戏开发/a项目创建/2-4.png)

### （二）角色创建

#### 1. 资源处理

将下载的角色资源拖动到虚幻虚幻引擎中，并做初步地整理和重命名，删除不需要的内容。

![ ](images/游戏开发/b角色创建/1-1.png)

选中导入的纹理，右键选择应用Paper2D纹理设置。

![ ](images/游戏开发/b角色创建/1-2.png)

#### 2. 制作角色动画

因为角色动画制作都是重复操作，这里以攻击动画为例。全选文件夹中的所有纹理，右键选择创建Sprite。

![ ](images/游戏开发/b角色创建/2-1.png)

此时会得到多个精灵图，右键选择资产操作 > 编辑属性矩阵中的选择。

![ ](images/游戏开发/b角色创建/2-2.png)

在顶部菜单将窗口 > 细节打开，将所有精灵图的枢轴模式改为自定义(27, 43)（需要根据精灵图调节合适的数值）。

![ ](images/游戏开发/b角色创建/2-3.png)

右键将这些精灵图创建图像序列，双击图像序列，可以预览，在右侧细节面板可以通过设置精灵 > 每秒帧数来改变播放速率。

![ ](images/游戏开发/b角色创建/2-4.png)

#### 3. 创建角色蓝图类

在虚幻Fab中添加PaperZD插件，并在虚幻引擎顶部菜单栏编辑 > 插件中将PaperZD插件勾选上，重启虚幻引擎。

![ ](images/游戏开发/b角色创建/3-1.png)

在Blueprints目录下新建蓝图类，选择PaperZDCharacter作为父类，将该蓝图命名为BP_Character。

![ ](images/游戏开发/b角色创建/3-2.png)

双击打开BP_GameMode，BP_Character设为默认pawn类。

![ ](images/游戏开发/b角色创建/3-3.png)

双击打开BP_Character，在左侧组件面板选择Sprite，在右侧细节面板设置源图像序列视图为Warrior_Idle。

![ ](images/游戏开发/b角色创建/3-4.png)

切换至右视图，调整Sprite高度、位置以适应胶囊体高度，同时调整胶囊体半径以适应角色长宽。

![ ](images/游戏开发/b角色创建/3-5.png)

![ ](images/游戏开发/b角色创建/3-6.png)

在左侧组件面板添加弹簧臂和相机组件，调整弹簧臂的旋转和目标臂长度，关闭进行碰撞检测。

![ ](images/游戏开发/b角色创建/3-7.png)

#### 4. 为角色添加光照投影

将Sprite材质选取为MaskedLitSpriteMaterial。

![ ](images/游戏开发/b角色创建/4-1.png)

勾选投射阴影。

![ ](images/游戏开发/b角色创建/4-2.png)

### （三）玩家输入

#### 1. 新建输入映射情景

在内容文件夹下新建Input文件夹，在其中新建输入 > 输入映射情景，命名为IMC_Player。

![ ](images/游戏开发/c玩家输入/1-1.png)

在Input文件夹新建Actions文件夹，在其中新建输入 > 输入操作IA_Dash、IA_Jump和IA_Move，分别表示角色突进、跳跃和移动操作。

![ ](images/游戏开发/c玩家输入/1-2.png)

双击打开IA_Move，将操作 > 值类型改为Axis2D，使角色支持四方向移动。

![ ](images/游戏开发/c玩家输入/1-3.png)

双击打开IMC_Player，将输入操作添加到映射中。

- IA_Dash绑定为左Shift

- IA_Jump绑定为空格键

- IA_Move绑定了WASD四个键：
  W键添加了拌合输入轴值和否定的修改器
  A键添加了否定的修改器
  S键添加了拌合输入轴值的修改器
  D键没有任何修改器

![ ](images/游戏开发/c玩家输入/1-4.png)

#### 2. 创建玩家控制器

在Blueprints目录下新建玩家控制器，命名为BP_PlayerController。

![ ](images/游戏开发/c玩家输入/2-1.png)

双击打开BP_GameMode，将玩家控制器类改为BP_PlayerController。

![ ](images/游戏开发/c玩家输入/2-2.png)

#### 3. 获取玩家控制器并添加映射上下文

双击打开Blueprints/BP_Character，在事件已控制时获取玩家控制器并为其增强输入本地玩家子系统添加映射上下文。

![ ](images/游戏开发/c玩家输入/3-1.png)

#### 4. 角色增强输入操作

IA_Dash：角色向前方弹射

IA_Jump：角色跳跃
IA_Move：角色旋转、移动

![ ](images/游戏开发/c玩家输入/4-1.png)

### （四）角色动画

#### 1. 创建动画源

在Animation/Warrior下创建PaperZD AnimationSource，命名为AS_Warrior。

![ ](images/游戏开发/d角色动画/1-1.png)

双击打开AS_Warrior，点击左侧Add New添加新的动画源，并在右侧选择对应的纸片图像序列。

![ ](images/游戏开发/d角色动画/1-2.png)

#### 2. 创建动画蓝图

在同一目录下创建PaperZD AnimBP，选择AS_Warrior为其动画源，命名为ABP_Warrior。

![ ](images/游戏开发/d角色动画/2-1.png)

打开Blueprints/BP_Character，将其Animation Component的PaperZD > Anim Instance Class设为ABP_Warrior。

![ ](images/游戏开发/d角色动画/2-2.png)

#### 3. 创建状态机

打开ABP_Warrior，添加变量并在事件图表中编辑。

![ ](images/游戏开发/d角色动画/3-1.png)

在AnimGraph中创建状态机Character State Machine，并连接到Output Animation。

![ ](images/游戏开发/d角色动画/3-2.png)

双击打开Character State Machine，创建多个Animation State，重命名并连接。

![ ](images/游戏开发/d角色动画/3-3.png)

对于每一个Animation State，都会直接播放对应的动画，Jump是例外，它会根据当前是否下落，决定播放下落动画还是跳跃动画。

![ ](images/游戏开发/d角色动画/3-4.png)

状态间的切换是通过创建的变量来实现的。

![ ](images/游戏开发/d角色动画/3-5.png)

### （五）角色攻击

在Input/Actions中创建IA_Attack。

![ ](images/游戏开发/e角色攻击/1-1.png)

打开Input/IMC_Player，添加映射，将IA_Attack绑定到鼠标左键。

![ ](images/游戏开发/e角色攻击/1-2.png)

打开Animation/Warrior/ABP_Warrior，在状态机输出前添加重写默认插槽。

![ ](images/游戏开发/e角色攻击/1-3.png)

打开Blueprints/BP_Character，添加增强输入事件IA_Attack。同时，修改增强输入事件IA_Dash的逻辑，将其改为仅在角色非攻击、非突进、速度不为0时触发，并将动画播放功能放到默认插槽。

![ ](images/游戏开发/e角色攻击/1-4.png)

将增强输入事件IA_Jump和IA_Move改为在非重击时触发，在轻击时，取消攻击并触发后续动作。

![ ](images/游戏开发/e角色攻击/1-5.png)

![ ](images/游戏开发/e角色攻击/1-6.png)

打开Animation/Warrior/ABP_Warrior，将与Dash有关的部分都删除。

![ ](images/游戏开发/e角色攻击/1-7.png)

![ ](images/游戏开发/e角色攻击/1-8.png)

### （六）角色动画音效

将下载的音效导入到Assets/SoundEffects并做重命名处理。

![ ](images/游戏开发/f角色动画音效/1-1.png)

打开Animation/Warrior/AS_Warrior，这里以攻击动画为例，添加动画音效。定位到挥剑的动作，右键轨道1，选择添加通知 > Play Sound。

![ ](images/游戏开发/f角色动画音效/1-2.png)

选中创建的通知，在右下角选择刚刚导入的音效，反复调节效果。同样地，对其他动画做同样的处理。

![ ](images/游戏开发/f角色动画音效/1-3.png)

在Blueprints/BP_Character中，为突进重击添加停止跳跃和快速落地的效果；为跳跃添加音效，并限制只能在Z轴速度为0时跳跃。

![ ](images/游戏开发/f角色动画音效/1-4.png)