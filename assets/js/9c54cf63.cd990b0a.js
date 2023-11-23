"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[899],{8759:(e,n,i)=>{i.r(n),i.d(n,{assets:()=>r,contentTitle:()=>t,default:()=>h,frontMatter:()=>o,metadata:()=>l,toc:()=>c});var s=i(5893),a=i(1151);const o={sidebar_position:1},t="Configuration Basics",l={id:"config/basics",title:"Configuration Basics",description:"ECInventory uses [HOCON] format for configuration files.",source:"@site/docs/config/basics.md",sourceDirName:"config",slug:"/config/basics",permalink:"/ECInventory/docs/config/basics",draft:!1,unlisted:!1,editUrl:"https://github.com/EndlessCodeGroup/ECInventory/tree/develop/website/docs/config/basics.md",tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1},sidebar:"tutorialSidebar",previous:{title:"Commands and permissions",permalink:"/ECInventory/docs/usage/commands"},next:{title:"Plugin configuration",permalink:"/ECInventory/docs/config/plugin"}},r={},c=[{value:"HOCON basics",id:"hocon-basics",level:2},{value:"Objects",id:"objects",level:3},{value:"Arrays and lists",id:"arrays-and-lists",level:3},{value:"List of objects",id:"list",level:3},{value:"Types",id:"types",level:2},{value:"String",id:"string",level:3},{value:"Number",id:"number",level:3},{value:"Boolean",id:"boolean",level:3},{value:"Item",id:"item",level:3},{value:"Advanced HOCON",id:"advanced-hocon",level:2},{value:"Path as key",id:"path-as-key",level:3},{value:"Substitutions",id:"substitutions",level:3},{value:"Concatenation and inheritance",id:"concatenation-and-inheritance",level:3}];function d(e){const n={a:"a",admonition:"admonition",code:"code",h1:"h1",h2:"h2",h3:"h3",li:"li",p:"p",pre:"pre",ul:"ul",...(0,a.a)(),...e.components};return(0,s.jsxs)(s.Fragment,{children:[(0,s.jsx)(n.h1,{id:"configuration-basics",children:"Configuration Basics"}),"\n",(0,s.jsxs)(n.p,{children:["ECInventory uses ",(0,s.jsx)(n.a,{href:"https://github.com/lightbend/config/blob/main/HOCON.md",children:"HOCON"})," format for configuration files.\nConfiguration files use the file extension ",(0,s.jsx)(n.code,{children:".conf"}),"."]}),"\n",(0,s.jsx)(n.h2,{id:"hocon-basics",children:"HOCON basics"}),"\n",(0,s.jsx)(n.p,{children:"HOCON (Human-Optimized Config Object Notation) is a human-friendly configuration format, and a superset of JSON."}),"\n",(0,s.jsx)(n.admonition,{type:"tip",children:(0,s.jsxs)(n.p,{children:["This is a brief HOCON format description targeted on users who already familiar with YAML.\nIf you want to know all about HOCON, ",(0,s.jsx)(n.a,{href:"https://github.com/lightbend/config/blob/main/HOCON.md",children:"read the specification"}),"."]})}),"\n",(0,s.jsxs)(n.p,{children:["Properties in HOCON has ",(0,s.jsx)(n.code,{children:"key"})," and ",(0,s.jsx)(n.code,{children:"value"})," separated by ",(0,s.jsx)(n.code,{children:"key-value separator"}),":"]}),"\n",(0,s.jsxs)(n.ul,{children:["\n",(0,s.jsxs)(n.li,{children:["a ",(0,s.jsx)(n.code,{children:"key"})," is a string describing ",(0,s.jsx)(n.code,{children:"value"})," destination"]}),"\n",(0,s.jsxs)(n.li,{children:["a ",(0,s.jsx)(n.code,{children:"value"})," may be string, number, object, boolean, enumeration type or ",(0,s.jsx)(n.code,{children:"null"})]}),"\n",(0,s.jsxs)(n.li,{children:["a ",(0,s.jsx)(n.code,{children:"key-value separator"})," separates key and value, should be either ",(0,s.jsx)(n.code,{children:":"})," (YAML-like), or ",(0,s.jsx)(n.code,{children:"="})," (JSON-like)"]}),"\n"]}),"\n",(0,s.jsxs)(n.p,{children:["If line in config starts with ",(0,s.jsx)(n.code,{children:"//"})," or ",(0,s.jsx)(n.code,{children:"#"}),", it is considered a comment."]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'# This is a comment. It can contain additional information about property.\n# Here "display-name" is a key, ":" is a key-value separator and "My beautiful inventory" is a value.\ndisplay-name: "My beautiful inventory"\n'})}),"\n",(0,s.jsx)(n.p,{children:"HOCON config may be very similar to YAML, but it has significant differences in objects and lists declaration."}),"\n",(0,s.jsx)(n.h3,{id:"objects",children:"Objects"}),"\n",(0,s.jsxs)(n.p,{children:["YAML uses indentation for object declaration, but HOCON uses curly braces ",(0,s.jsx)(n.code,{children:"{}"}),":"]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'## YAML\nmy-slot:\n  display-name: "Custom slot"\n  type: storage\n\n## HOCON\nmy-slot {\n  display-name: "Custom slot"\n  type: storage\n}\n# HOCON also supports one-line object declaration\nmy-slot { display-name: "Custom slot", type: storage }\n'})}),"\n",(0,s.jsx)(n.h3,{id:"arrays-and-lists",children:"Arrays and lists"}),"\n",(0,s.jsxs)(n.p,{children:["YAML has two notations to declare list elements \u2014 using square braces ",(0,s.jsx)(n.code,{children:"[]"})," or using hyphen ",(0,s.jsx)(n.code,{children:"-"}),' at the line start.\nHOCON supports only "square braces" style:']}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'## YAML\ndescription:\n  - "First line"  \n  - "Second line"\n# One-line list declaration also supported\ndescription: ["First line", "Second line"]\n\n## HOCON\ndescription: [\n  "First line",\n  "Second line"\n]\n# One-line list declaration looks exactly like in YAML\ndescription: ["First line", "Second line"]\n'})}),"\n",(0,s.jsx)(n.h3,{id:"list",children:"List of objects"}),"\n",(0,s.jsx)(n.p,{children:"YAML allows declaring list of objects using hyphen list notation, but in HOCON we should use curly braces to declare objects in list:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'## YAML\nactions:\n  - on: [right_click]\n    do: ["weather clear"]\n\n## HOCON\nactions: [\n  {\n      on: [right_click]\n      do: ["weather clear"]\n  }\n]\n# Or more compact variant using one-line object declaration\nactions: [\n  {on: [right_click], do: ["weather_clear"]}\n]\n'})}),"\n",(0,s.jsx)(n.h2,{id:"types",children:"Types"}),"\n",(0,s.jsx)(n.p,{children:"Here are listed common types used in configs.\nOther types are described in the place of usage."}),"\n",(0,s.jsx)(n.h3,{id:"string",children:"String"}),"\n",(0,s.jsx)(n.p,{children:"Strings may be quoted and unquoted.\nIt is recommended to always use quoted strings because unquoted strings has limited set of characters they can contain."}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'quoted-string: "This is a string value"\nunquoted-string: This also allowed but not recommended\n'})}),"\n",(0,s.jsx)(n.h3,{id:"number",children:"Number"}),"\n",(0,s.jsx)(n.p,{children:"Numbers may be either integer, or with floating point.\nAllowed range usually specified in field specification."}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:"integer-value: 42\nfloat-value: 0.5\n"})}),"\n",(0,s.jsx)(n.h3,{id:"boolean",children:"Boolean"}),"\n",(0,s.jsxs)(n.p,{children:["Primitive logical type that can have only the values ",(0,s.jsx)(n.code,{children:"true"})," (aliases: ",(0,s.jsx)(n.code,{children:"yes"}),", ",(0,s.jsx)(n.code,{children:"on"}),") or ",(0,s.jsx)(n.code,{children:"false"})," (aliases: ",(0,s.jsx)(n.code,{children:"no"}),", ",(0,s.jsx)(n.code,{children:"off"}),")."]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:"boolean-field: true\n"})}),"\n",(0,s.jsx)(n.h3,{id:"item",children:"Item"}),"\n",(0,s.jsxs)(n.p,{children:["Item ID that can be used to obtain item via ",(0,s.jsx)(n.a,{href:"https://www.spigotmc.org/resources/82515/",children:"Mimic"}),".\nYou can add namespace if you want to get item from the defined source."]}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:"minecraft-item: minecraft:iron_sword\ncustom-item: mmoitems:iron_sword\nitem-without-namespace: iron_sword\n"})}),"\n",(0,s.jsxs)(n.admonition,{type:"tip",children:[(0,s.jsx)(n.p,{children:"You can check list of available items using command:"}),(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{children:"/mimic item give <player> <item_id>\n"})})]}),"\n",(0,s.jsx)(n.h2,{id:"advanced-hocon",children:"Advanced HOCON"}),"\n",(0,s.jsx)(n.p,{children:"HOCON provides features good to know because it may be useful when you configure the plugin."}),"\n",(0,s.jsx)(n.h3,{id:"path-as-key",children:"Path as key"}),"\n",(0,s.jsx)(n.p,{children:"You can use paths as a keys for values to configure nested objects:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'my-slot {\n  display-name: "Change my type"\n}\n\n# HOCON will go to the "my-slot" and change its property called "type"\nmy-slot.type: generic\n\n# This is also a valid notation to declare "other-slot" object\nother-slot.display-name: "Slot created by path keys"\nother-slot.type: generic\n'})}),"\n",(0,s.jsx)(n.h3,{id:"substitutions",children:"Substitutions"}),"\n",(0,s.jsxs)(n.p,{children:["HOCON allows referring from value to other paths in configuration.\nReferent path should be in format ",(0,s.jsx)(n.code,{children:"${absolute.path.to.field}"}),"."]}),"\n",(0,s.jsx)(n.p,{children:"For example, you can create a variable and reuse it in several values:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'server-name: "Best Server"\n\nserver-info-slot {\n  display-name: ${server-name} info\n  description: ["Server name is "${server-name}]\n  type: gui\n}\n'})}),"\n",(0,s.jsxs)(n.admonition,{type:"tip",children:[(0,s.jsxs)(n.p,{children:["Substitutions are not allowed inside quotes ",(0,s.jsx)(n.code,{children:'"'}),".\nSo you should move substitution out of the quotes the following way:"]}),(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'# Bad\n"Server name is ${server-name}!"\n\n# Good\n"Server name is "${server-name}"!"\n'})})]}),"\n",(0,s.jsx)(n.h3,{id:"concatenation-and-inheritance",children:"Concatenation and inheritance"}),"\n",(0,s.jsxs)(n.p,{children:["HOCON allows concatenating values including objects and lists.\nIt is a powerful feature in combination with ",(0,s.jsx)(n.a,{href:"#substitutions",children:"substitutions"}),"."]}),"\n",(0,s.jsx)(n.p,{children:"Lists concatenation can be used to share common configurations:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:"offhand {\n  allowed-items: [dagger, shield]\n}\n\n# We can hold in main hand everything we can hold in offhand and sword or axe additionally\nmainhand {\n  allowed-items: ${offhand.allowed-items} [sword, axe]\n}\n"})}),"\n",(0,s.jsx)(n.p,{children:"Objects concatenation can be used for inheritance:"}),"\n",(0,s.jsx)(n.pre,{children:(0,s.jsx)(n.code,{className:"language-yaml",children:'ammo-base {\n  type: generic\n  max-stack-size: 16\n}\n\n# Arrows slot will inherit all fields from "ammo-base"\narrows: ${ammo-base} {\n  allowed-items: [arrow]\n}\n'})}),"\n",(0,s.jsx)(n.admonition,{type:"tip",children:(0,s.jsxs)(n.p,{children:["Remember you should use absolute path to objects for substitution.\nIn real configs paths will look like ",(0,s.jsx)(n.code,{children:"slots.ammo-base"})," instead of ",(0,s.jsx)(n.code,{children:"ammo-base"}),"."]})})]})}function h(e={}){const{wrapper:n}={...(0,a.a)(),...e.components};return n?(0,s.jsx)(n,{...e,children:(0,s.jsx)(d,{...e})}):d(e)}},1151:(e,n,i)=>{i.d(n,{Z:()=>l,a:()=>t});var s=i(7294);const a={},o=s.createContext(a);function t(e){const n=s.useContext(o);return s.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:t(e.components),s.createElement(o.Provider,{value:n},e.children)}}}]);