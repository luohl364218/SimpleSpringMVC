**思路介绍**

(1).首先我们需要扫描包中的所有文件`

（DispatcherServlet->init(ServletConfig config)->scanPackage("com.heylink")）

也就是含有注解的文件。然后将该包下的所有文件都存入packageNames集合中。

(2).这时我们拿到了包下所有的文件，但我们只需要含有我们指定注解的那部分文件，因此需要过滤出我们想要的文件即可，并且在过滤的过程中，我们可以将过滤出来的类通过Class.forName来直接实例化并储存起来。存放到instanceMap集合中，并为其设置对应的key值，该key值就是类注解的value。

(3).然后遍历instanceMap集合中的所有对象，获取指定注解的对象，并通过反射获取该对象的所有的方法，遍历所有的方法，将指定注解的方法存入handerMap，key为拼接字符串（"/" + 对象变量名 + "/" + 方法名），value为方法（Method）。

(4).然后我们可以遍历instanceMap集合中的所有对象，通过反射获取对象的所有属性值（字段）集合，然后遍历属性值集合，将属性值含有指定注解的，通过Field的set方法为该属性值赋值，这时就将对象注入了。（也就是往Controller中注入Service对象）

(5).最后就可以通过反射的invoke方法调用某个对象的某个方法。（此时对象存于instanceMap，而方法都存于handerMap）