function org_akaza_openclinica_gwt_GwtMenu(){var l='',F='" for "gwt:onLoadErrorFn"',D='" for "gwt:onPropertyErrorFn"',n='"><\/script>',p='#',gb='&',r='/',Db='41A23D0DB62FE799D8AF2BCB22C49A57.cache.html',xb='6595AEB3A6A31413444D54783C302C48.cache.html',Eb='7C83D075C40C9405E00DF044FD8F406D.cache.html',bc='<script defer="defer">org_akaza_openclinica_gwt_GwtMenu.onInjectionDone(\'com.liteoc.gwt.GwtMenu\')<\/script>',fc='<script id="',A='=',q='?',C='Bad handler "',Fb='C1BFF84514E318F9EA7C8F8C428F6CC5.cache.html',ac='DOMContentLoaded',o='SCRIPT',jb='Unexpected exception in locale detection, using default: ',ib='_',ec='__gwt_marker_org.akaza.openclinica.gwt.GwtMenu',s='base',nb='begin',cb='bootstrap',u='clear.cache.gif',z='content',hb='default',zb='en',dc='end',Cb='es',Ab='fr',rb='gecko',sb='gecko1_8',yb='gwt.hybrid',E='gwt:onLoadErrorFn',B='gwt:onPropertyErrorFn',y='gwt:property',wb='hosted.html?org_akaza_openclinica_gwt_GwtMenu',qb='ie6',ab='iframe',t='img',Bb='it',bb="javascript:''",ub='loadExternalRefs',fb='locale',v='meta',eb='moduleRequested',cc='moduleStartup',pb='msie',w='name',lb='opera',m='com.liteoc.gwt.GwtMenu',db='position:absolute;width:0;height:0;border:none',ob='safari',vb='selectingPermutation',x='startup',tb='unknown',kb='user.agent',mb='webkit';var hc=window,k=document,gc=hc.__gwtStatsEvent?function(a){return hc.__gwtStatsEvent(a)}:null,Bc,rc,mc,lc=l,uc={},Ec=[],Ac=[],kc=[],xc,zc;gc&&gc({moduleName:m,subSystem:x,evtGroup:cb,millis:(new Date()).getTime(),type:nb});if(!hc.__gwt_stylesLoaded){hc.__gwt_stylesLoaded={}}if(!hc.__gwt_scriptsLoaded){hc.__gwt_scriptsLoaded={}}function qc(){try{return hc.external&&(hc.external.gwtOnLoad&&hc.location.search.indexOf(yb)==-1)}catch(a){return false}}
function tc(){if(Bc&&rc){var c=k.getElementById(m);var b=c.contentWindow;b.__gwt_initHandlers=org_akaza_openclinica_gwt_GwtMenu.__gwt_initHandlers;if(qc()){b.__gwt_getProperty=function(a){return nc(a)}}org_akaza_openclinica_gwt_GwtMenu=null;b.gwtOnLoad(xc,m,lc);gc&&gc({moduleName:m,subSystem:x,evtGroup:cc,millis:(new Date()).getTime(),type:dc})}}
function oc(){var j,h=ec,i;k.write(fc+h+n);i=k.getElementById(h);j=i&&i.previousSibling;while(j&&j.tagName!=o){j=j.previousSibling}function f(b){var a=b.lastIndexOf(p);if(a==-1){a=b.length}var c=b.indexOf(q);if(c==-1){c=b.length}var d=b.lastIndexOf(r,Math.min(c,a));return d>=0?b.substring(0,d+1):l}
;if(j&&j.src){lc=f(j.src)}if(lc==l){var e=k.getElementsByTagName(s);if(e.length>0){lc=e[e.length-1].href}else{lc=f(k.location.href)}}else if(lc.match(/^\w+:\/\//)){}else{var g=k.createElement(t);g.src=lc+u;lc=f(g.src)}if(i){i.parentNode.removeChild(i)}}
function yc(){var f=document.getElementsByTagName(v);for(var d=0,g=f.length;d<g;++d){var e=f[d],h=e.getAttribute(w),b;if(h){if(h==y){b=e.getAttribute(z);if(b){var i,c=b.indexOf(A);if(c>=0){h=b.substring(0,c);i=b.substring(c+1)}else{h=b;i=l}uc[h]=i}}else if(h==B){b=e.getAttribute(z);if(b){try{zc=eval(b)}catch(a){alert(C+b+D)}}}else if(h==E){b=e.getAttribute(z);if(b){try{xc=eval(b)}catch(a){alert(C+b+F)}}}}}}
function jc(a,b){return b in Ec[a]}
function ic(a){var b=uc[a];return b==null?null:b}
function Dc(d,e){var a=kc;for(var b=0,c=d.length-1;b<c;++b){a=a[d[b]]||(a[d[b]]=[])}a[d[c]]=e}
function nc(d){var e=Ac[d](),b=Ec[d];if(e in b){return e}var a=[];for(var c in b){a[b[c]]=c}if(zc){zc(d,a,e)}throw null}
var pc;function sc(){if(!pc){pc=true;var a=k.createElement(ab);a.src=bb;a.id=m;a.style.cssText=db;a.tabIndex=-1;k.body.appendChild(a);gc&&gc({moduleName:m,subSystem:x,evtGroup:cc,millis:(new Date()).getTime(),type:eb});a.contentWindow.location.replace(lc+Cc)}}
Ac[fb]=function(){try{var g;if(g==null){var b=location.search;var h=b.indexOf(fb);if(h>=0){var e=b.substring(h);var c=e.indexOf(A)+1;var d=e.indexOf(gb);if(d==-1){d=e.length}g=e.substring(c,d)}}if(g==null){g=ic(fb)}if(g==null){return hb}while(!jc(fb,g)){var f=g.lastIndexOf(ib);if(f==-1){g=hb;break}else{g=g.substring(0,f)}}return g}catch(a){alert(jb+a);return hb}};Ec[fb]={'default':0,en:1,es:2,fr:3,it:4};Ac[kb]=function(){var d=navigator.userAgent.toLowerCase();var b=function(a){return parseInt(a[1])*1000+parseInt(a[2])};if(d.indexOf(lb)!=-1){return lb}else if(d.indexOf(mb)!=-1){return ob}else if(d.indexOf(pb)!=-1){var c=/msie ([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=6000){return qb}}}else if(d.indexOf(rb)!=-1){var c=/rv:([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=1008)return sb}return rb}return tb};Ec[kb]={gecko:0,gecko1_8:1,ie6:2,opera:3,safari:4};org_akaza_openclinica_gwt_GwtMenu.onScriptLoad=function(){if(pc){rc=true;tc()}};org_akaza_openclinica_gwt_GwtMenu.onInjectionDone=function(){Bc=true;gc&&gc({moduleName:m,subSystem:x,evtGroup:ub,millis:(new Date()).getTime(),type:dc});tc()};oc();yc();gc&&gc({moduleName:m,subSystem:x,evtGroup:cb,millis:(new Date()).getTime(),type:vb});var Cc;if(qc()){Cc=wb}else{try{Dc([hb,qb],xb);Dc([zb,qb],xb);Dc([Ab,qb],xb);Dc([Bb,qb],xb);Dc([Cb,qb],Db);Dc([hb,rb],Eb);Dc([hb,sb],Eb);Dc([hb,lb],Eb);Dc([hb,ob],Eb);Dc([zb,rb],Eb);Dc([zb,sb],Eb);Dc([zb,lb],Eb);Dc([zb,ob],Eb);Dc([Ab,rb],Eb);Dc([Ab,sb],Eb);Dc([Ab,lb],Eb);Dc([Ab,ob],Eb);Dc([Bb,rb],Eb);Dc([Bb,sb],Eb);Dc([Bb,lb],Eb);Dc([Bb,ob],Eb);Dc([Cb,rb],Fb);Dc([Cb,sb],Fb);Dc([Cb,lb],Fb);Dc([Cb,ob],Fb);Cc=kc[nc(fb)][nc(kb)]}catch(a){return}}var wc;function vc(){if(!mc){mc=true;tc();if(k.removeEventListener){k.removeEventListener(ac,vc,false)}if(wc){clearInterval(wc)}}}
if(k.addEventListener){k.addEventListener(ac,function(){sc();vc()},false)}var wc=setInterval(function(){if(/loaded|complete/.test(k.readyState)){sc();vc()}},50);gc&&gc({moduleName:m,subSystem:x,evtGroup:cb,millis:(new Date()).getTime(),type:dc});gc&&gc({moduleName:m,subSystem:x,evtGroup:ub,millis:(new Date()).getTime(),type:nb});k.write(bc)}
org_akaza_openclinica_gwt_GwtMenu.__gwt_initHandlers=function(i,e,j){var d=window,g=d.onresize,f=d.onbeforeunload,h=d.onunload;d.onresize=function(a){try{i()}finally{g&&g(a)}};d.onbeforeunload=function(a){var c,b;try{c=e()}finally{b=f&&f(a)}if(c!=null){return c}if(b!=null){return b}};d.onunload=function(a){try{j()}finally{h&&h(a);d.onresize=null;d.onbeforeunload=null;d.onunload=null}}};org_akaza_openclinica_gwt_GwtMenu();