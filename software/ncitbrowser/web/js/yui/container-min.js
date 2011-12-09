/*
Copyright (c) 2011, Yahoo! Inc. All rights reserved.
Code licensed under the BSD License:
http://developer.yahoo.com/yui/license.html
version: 2.9.0
*/
(function () {
    YAHOO.util.Config = function (d) {
        if (d) {
            this.init(d);
        }
    };
    var b = YAHOO.lang,
        c = YAHOO.util.CustomEvent,
        a = YAHOO.util.Config;
    a.CONFIG_CHANGED_EVENT = "configChanged";
    a.BOOLEAN_TYPE = "boolean";
    a.prototype = {
        owner: null,
        queueInProgress: false,
        config: null,
        initialConfig: null,
        eventQueue: null,
        configChangedEvent: null,
        init: function (d) {
            this.owner = d;
            this.configChangedEvent = this.createEvent(a.CONFIG_CHANGED_EVENT);
            this.configChangedEvent.signature = c.LIST;
            this.queueInProgress = false;
            this.config = {};
            this.initialConfig = {};
            this.eventQueue = [];
        },
        checkBoolean: function (d) {
            return (typeof d == a.BOOLEAN_TYPE);
        },
        checkNumber: function (d) {
            return (!isNaN(d));
        },
        fireEvent: function (d, f) {
            var e = this.config[d];
            if (e && e.event) {
                e.event.fire(f);
            }
        },
        addProperty: function (e, d) {
            e = e.toLowerCase();
            this.config[e] = d;
            d.event = this.createEvent(e, {
                scope: this.owner
            });
            d.event.signature = c.LIST;
            d.key = e;
            if (d.handler) {
                d.event.subscribe(d.handler, this.owner);
            }
            this.setProperty(e, d.value, true);
            if (!d.suppressEvent) {
                this.queueProperty(e, d.value);
            }
        },
        getConfig: function () {
            var d = {},
                f = this.config,
                g, e;
            for (g in f) {
                if (b.hasOwnProperty(f, g)) {
                    e = f[g];
                    if (e && e.event) {
                        d[g] = e.value;
                    }
                }
            }
            return d;
        },
        getProperty: function (d) {
            var e = this.config[d.toLowerCase()];
            if (e && e.event) {
                return e.value;
            } else {
                return undefined;
            }
        },
        resetProperty: function (d) {
            d = d.toLowerCase();
            var e = this.config[d];
            if (e && e.event) {
                if (d in this.initialConfig) {
                    this.setProperty(d, this.initialConfig[d]);
                    return true;
                }
            } else {
                return false;
            }
        },
        setProperty: function (e, g, d) {
            var f;
            e = e.toLowerCase();
            if (this.queueInProgress && !d) {
                this.queueProperty(e, g);
                return true;
            } else {
                f = this.config[e];
                if (f && f.event) {
                    if (f.validator && !f.validator(g)) {
                        return false;
                    } else {
                        f.value = g;
                        if (!d) {
                            this.fireEvent(e, g);
                            this.configChangedEvent.fire([e, g]);
                        }
                        return true;
                    }
                } else {
                    return false;
                }
            }
        },
        queueProperty: function (v, r) {
            v = v.toLowerCase();
            var u = this.config[v],
                l = false,
                k, g, h, j, p, t, f, n, o, d, m, w, e;
            if (u && u.event) {
                if (!b.isUndefined(r) && u.validator && !u.validator(r)) {
                    return false;
                } else {
                    if (!b.isUndefined(r)) {
                        u.value = r;
                    } else {
                        r = u.value;
                    }
                    l = false;
                    k = this.eventQueue.length;
                    for (m = 0; m < k; m++) {
                        g = this.eventQueue[m];
                        if (g) {
                            h = g[0];
                            j = g[1];
                            if (h == v) {
                                this.eventQueue[m] = null;
                                this.eventQueue.push([v, (!b.isUndefined(r) ? r : j)]);
                                l = true;
                                break;
                            }
                        }
                    }
                    if (!l && !b.isUndefined(r)) {
                        this.eventQueue.push([v, r]);
                    }
                }
                if (u.supercedes) {
                    p = u.supercedes.length;
                    for (w = 0; w < p; w++) {
                        t = u.supercedes[w];
                        f = this.eventQueue.length;
                        for (e = 0; e < f; e++) {
                            n = this.eventQueue[e];
                            if (n) {
                                o = n[0];
                                d = n[1];
                                if (o == t.toLowerCase()) {
                                    this.eventQueue.push([o, d]);
                                    this.eventQueue[e] = null;
                                    break;
                                }
                            }
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        },
        refireEvent: function (d) {
            d = d.toLowerCase();
            var e = this.config[d];
            if (e && e.event && !b.isUndefined(e.value)) {
                if (this.queueInProgress) {
                    this.queueProperty(d);
                } else {
                    this.fireEvent(d, e.value);
                }
            }
        },
        applyConfig: function (d, g) {
            var f, e;
            if (g) {
                e = {};
                for (f in d) {
                    if (b.hasOwnProperty(d, f)) {
                        e[f.toLowerCase()] = d[f];
                    }
                }
                this.initialConfig = e;
            }
            for (f in d) {
                if (b.hasOwnProperty(d, f)) {
                    this.queueProperty(f, d[f]);
                }
            }
        },
        refresh: function () {
            var d;
            for (d in this.config) {
                if (b.hasOwnProperty(this.config, d)) {
                    this.refireEvent(d);
                }
            }
        },
        fireQueue: function () {
            var e, h, d, g, f;
            this.queueInProgress = true;
            for (e = 0; e < this.eventQueue.length; e++) {
                h = this.eventQueue[e];
                if (h) {
                    d = h[0];
                    g = h[1];
                    f = this.config[d];
                    f.value = g;
                    this.eventQueue[e] = null;
                    this.fireEvent(d, g);
                }
            }
            this.queueInProgress = false;
            this.eventQueue = [];
        },
        subscribeToConfigEvent: function (d, e, g, h) {
            var f = this.config[d.toLowerCase()];
            if (f && f.event) {
                if (!a.alreadySubscribed(f.event, e, g)) {
                    f.event.subscribe(e, g, h);
                }
                return true;
            } else {
                return false;
            }
        },
        unsubscribeFromConfigEvent: function (d, e, g) {
            var f = this.config[d.toLowerCase()];
            if (f && f.event) {
                return f.event.unsubscribe(e, g);
            } else {
                return false;
            }
        },
        toString: function () {
            var d = "Config";
            if (this.owner) {
                d += " [" + this.owner.toString() + "]";
            }
            return d;
        },
        outputEventQueue: function () {
            var d = "",
                g, e, f = this.eventQueue.length;
            for (e = 0; e < f; e++) {
                g = this.eventQueue[e];
                if (g) {
                    d += g[0] + "=" + g[1] + ", ";
                }
            }
            return d;
        },
        destroy: function () {
            var e = this.config,
                d, f;
            for (d in e) {
                if (b.hasOwnProperty(e, d)) {
                    f = e[d];
                    f.event.unsubscribeAll();
                    f.event = null;
                }
            }
            this.configChangedEvent.unsubscribeAll();
            this.configChangedEvent = null;
            this.owner = null;
            this.config = null;
            this.initialConfig = null;
            this.eventQueue = null;
        }
    };
    a.alreadySubscribed = function (e, h, j) {
        var f = e.subscribers.length,
            d, g;
        if (f > 0) {
            g = f - 1;
            do {
                d = e.subscribers[g];
                if (d && d.obj == j && d.fn == h) {
                    return true;
                }
            } while (g--);
        }
        return false;
    };
    YAHOO.lang.augmentProto(a, YAHOO.util.EventProvider);
}());
(function () {
    YAHOO.widget.Module = function (r, q) {
        if (r) {
            this.init(r, q);
        } else {}
    };
    var f = YAHOO.util.Dom,
        d = YAHOO.util.Config,
        n = YAHOO.util.Event,
        m = YAHOO.util.CustomEvent,
        g = YAHOO.widget.Module,
        i = YAHOO.env.ua,
        h, p, o, e, a = {
            "BEFORE_INIT": "beforeInit",
            "INIT": "init",
            "APPEND": "append",
            "BEFORE_RENDER": "beforeRender",
            "RENDER": "render",
            "CHANGE_HEADER": "changeHeader",
            "CHANGE_BODY": "changeBody",
            "CHANGE_FOOTER": "changeFooter",
            "CHANGE_CONTENT": "changeContent",
            "DESTROY": "destroy",
            "BEFORE_SHOW": "beforeShow",
            "SHOW": "show",
            "BEFORE_HIDE": "beforeHide",
            "HIDE": "hide"
        },
        j = {
            "VISIBLE": {
                key: "visible",
                value: true,
                validator: YAHOO.lang.isBoolean
            },
            "EFFECT": {
                key: "effect",
                suppressEvent: true,
                supercedes: ["visible"]
            },
            "MONITOR_RESIZE": {
                key: "monitorresize",
                value: true
            },
            "APPEND_TO_DOCUMENT_BODY": {
                key: "appendtodocumentbody",
                value: false
            }
        };
    g.IMG_ROOT = null;
    g.IMG_ROOT_SSL = null;
    g.CSS_MODULE = "yui-module";
    g.CSS_HEADER = "hd";
    g.CSS_BODY = "bd";
    g.CSS_FOOTER = "ft";
    g.RESIZE_MONITOR_SECURE_URL = "javascript:false;";
    g.RESIZE_MONITOR_BUFFER = 1;
    g.textResizeEvent = new m("textResize");
    g.forceDocumentRedraw = function () {
        var q = document.documentElement;
        if (q) {
            q.className += " ";
            q.className = YAHOO.lang.trim(q.className);
        }
    };

    function l() {
        if (!h) {
            h = document.createElement("div");
            h.innerHTML = ('<div class="' + g.CSS_HEADER + '"></div>' + '<div class="' + g.CSS_BODY + '"></div><div class="' + g.CSS_FOOTER + '"></div>');
            p = h.firstChild;
            o = p.nextSibling;
            e = o.nextSibling;
        }
        return h;
    }
    function k() {
        if (!p) {
            l();
        }
        return (p.cloneNode(false));
    }
    function b() {
        if (!o) {
            l();
        }
        return (o.cloneNode(false));
    }
    function c() {
        if (!e) {
            l();
        }
        return (e.cloneNode(false));
    }
    g.prototype = {
        constructor: g,
        element: null,
        header: null,
        body: null,
        footer: null,
        id: null,
        imageRoot: g.IMG_ROOT,
        initEvents: function () {
            var q = m.LIST;
            this.beforeInitEvent = this.createEvent(a.BEFORE_INIT);
            this.beforeInitEvent.signature = q;
            this.initEvent = this.createEvent(a.INIT);
            this.initEvent.signature = q;
            this.appendEvent = this.createEvent(a.APPEND);
            this.appendEvent.signature = q;
            this.beforeRenderEvent = this.createEvent(a.BEFORE_RENDER);
            this.beforeRenderEvent.signature = q;
            this.renderEvent = this.createEvent(a.RENDER);
            this.renderEvent.signature = q;
            this.changeHeaderEvent = this.createEvent(a.CHANGE_HEADER);
            this.changeHeaderEvent.signature = q;
            this.changeBodyEvent = this.createEvent(a.CHANGE_BODY);
            this.changeBodyEvent.signature = q;
            this.changeFooterEvent = this.createEvent(a.CHANGE_FOOTER);
            this.changeFooterEvent.signature = q;
            this.changeContentEvent = this.createEvent(a.CHANGE_CONTENT);
            this.changeContentEvent.signature = q;
            this.destroyEvent = this.createEvent(a.DESTROY);
            this.destroyEvent.signature = q;
            this.beforeShowEvent = this.createEvent(a.BEFORE_SHOW);
            this.beforeShowEvent.signature = q;
            this.showEvent = this.createEvent(a.SHOW);
            this.showEvent.signature = q;
            this.beforeHideEvent = this.createEvent(a.BEFORE_HIDE);
            this.beforeHideEvent.signature = q;
            this.hideEvent = this.createEvent(a.HIDE);
            this.hideEvent.signature = q;
        },
        platform: function () {
            var q = navigator.userAgent.toLowerCase();
            if (q.indexOf("windows") != -1 || q.indexOf("win32") != -1) {
                return "windows";
            } else {
                if (q.indexOf("macintosh") != -1) {
                    return "mac";
                } else {
                    return false;
                }
            }
        }(),
        browser: function () {
            var q = navigator.userAgent.toLowerCase();
            if (q.indexOf("opera") != -1) {
                return "opera";
            } else {
                if (q.indexOf("msie 7") != -1) {
                    return "ie7";
                } else {
                    if (q.indexOf("msie") != -1) {
                        return "ie";
                    } else {
                        if (q.indexOf("safari") != -1) {
                            return "safari";
                        } else {
                            if (q.indexOf("gecko") != -1) {
                                return "gecko";
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
        }(),
        isSecure: function () {
            if (window.location.href.toLowerCase().indexOf("https") === 0) {
                return true;
            } else {
                return false;
            }
        }(),
        initDefaultConfig: function () {
            this.cfg.addProperty(j.VISIBLE.key, {
                handler: this.configVisible,
                value: j.VISIBLE.value,
                validator: j.VISIBLE.validator
            });
            this.cfg.addProperty(j.EFFECT.key, {
                handler: this.configEffect,
                suppressEvent: j.EFFECT.suppressEvent,
                supercedes: j.EFFECT.supercedes
            });
            this.cfg.addProperty(j.MONITOR_RESIZE.key, {
                handler: this.configMonitorResize,
                value: j.MONITOR_RESIZE.value
            });
            this.cfg.addProperty(j.APPEND_TO_DOCUMENT_BODY.key, {
                value: j.APPEND_TO_DOCUMENT_BODY.value
            });
        },
        init: function (v, u) {
            var s, w;
            this.initEvents();
            this.beforeInitEvent.fire(g);
            this.cfg = new d(this);
            if (this.isSecure) {
                this.imageRoot = g.IMG_ROOT_SSL;
            }
            if (typeof v == "string") {
                s = v;
                v = document.getElementById(v);
                if (!v) {
                    v = (l()).cloneNode(false);
                    v.id = s;
                }
            }
            this.id = f.generateId(v);
            this.element = v;
            w = this.element.firstChild;
            if (w) {
                var r = false,
                    q = false,
                    t = false;
                do {
                    if (1 == w.nodeType) {
                        if (!r && f.hasClass(w, g.CSS_HEADER)) {
                            this.header = w;
                            r = true;
                        } else {
                            if (!q && f.hasClass(w, g.CSS_BODY)) {
                                this.body = w;
                                q = true;
                            } else {
                                if (!t && f.hasClass(w, g.CSS_FOOTER)) {
                                    this.footer = w;
                                    t = true;
                                }
                            }
                        }
                    }
                } while ((w = w.nextSibling));
            }
            this.initDefaultConfig();
            f.addClass(this.element, g.CSS_MODULE);
            if (u) {
                this.cfg.applyConfig(u, true);
            }
            if (!d.alreadySubscribed(this.renderEvent, this.cfg.fireQueue, this.cfg)) {
                this.renderEvent.subscribe(this.cfg.fireQueue, this.cfg, true);
            }
            this.initEvent.fire(g);
        },
        initResizeMonitor: function () {
            var r = (i.gecko && this.platform == "windows");
            if (r) {
                var q = this;
                setTimeout(function () {
                    q._initResizeMonitor();
                }, 0);
            } else {
                this._initResizeMonitor();
            }
        },
        _initResizeMonitor: function () {
            var q, s, u;

            function w() {
                g.textResizeEvent.fire();
            }
            if (!i.opera) {
                s = f.get("_yuiResizeMonitor");
                var v = this._supportsCWResize();
                if (!s) {
                    s = document.createElement("iframe");
                    if (this.isSecure && g.RESIZE_MONITOR_SECURE_URL && i.ie) {
                        s.src = g.RESIZE_MONITOR_SECURE_URL;
                    }
                    if (!v) {
                        u = ["<html><head><script ", 'type="text/javascript">', "window.onresize=function(){window.parent.", "YAHOO.widget.Module.textResizeEvent.", "fire();};<", "/script></head>", "<body></body></html>"].join("");
                        s.src = "data:text/html;charset=utf-8," + encodeURIComponent(u);
                    }
                    s.id = "_yuiResizeMonitor";
                    s.title = "Text Resize Monitor";
                    s.tabIndex = -1;
                    s.setAttribute("role", "presentation");
                    s.style.position = "absolute";
                    s.style.visibility = "hidden";
                    var r = document.body,
                        t = r.firstChild;
                    if (t) {
                        r.insertBefore(s, t);
                    } else {
                        r.appendChild(s);
                    }
                    s.style.backgroundColor = "transparent";
                    s.style.borderWidth = "0";
                    s.style.width = "2em";
                    s.style.height = "2em";
                    s.style.left = "0";
                    s.style.top = (-1 * (s.offsetHeight + g.RESIZE_MONITOR_BUFFER)) + "px";
                    s.style.visibility = "visible";
                    if (i.webkit) {
                        q = s.contentWindow.document;
                        q.open();
                        q.close();
                    }
                }
                if (s && s.contentWindow) {
                    g.textResizeEvent.subscribe(this.onDomResize, this, true);
                    if (!g.textResizeInitialized) {
                        if (v) {
                            if (!n.on(s.contentWindow, "resize", w)) {
                                n.on(s, "resize", w);
                            }
                        }
                        g.textResizeInitialized = true;
                    }
                    this.resizeMonitor = s;
                }
            }
        },
        _supportsCWResize: function () {
            var q = true;
            if (i.gecko && i.gecko <= 1.8) {
                q = false;
            }
            return q;
        },
        onDomResize: function (s, r) {
            var q = -1 * (this.resizeMonitor.offsetHeight + g.RESIZE_MONITOR_BUFFER);
            this.resizeMonitor.style.top = q + "px";
            this.resizeMonitor.style.left = "0";
        },
        setHeader: function (r) {
            var q = this.header || (this.header = k());
            if (r.nodeName) {
                q.innerHTML = "";
                q.appendChild(r);
            } else {
                q.innerHTML = r;
            }
            if (this._rendered) {
                this._renderHeader();
            }
            this.changeHeaderEvent.fire(r);
            this.changeContentEvent.fire();
        },
        appendToHeader: function (r) {
            var q = this.header || (this.header = k());
            q.appendChild(r);
            this.changeHeaderEvent.fire(r);
            this.changeContentEvent.fire();
        },
        setBody: function (r) {
            var q = this.body || (this.body = b());
            if (r.nodeName) {
                q.innerHTML = "";
                q.appendChild(r);
            } else {
                q.innerHTML = r;
            }
            if (this._rendered) {
                this._renderBody();
            }
            this.changeBodyEvent.fire(r);
            this.changeContentEvent.fire();
        },
        appendToBody: function (r) {
            var q = this.body || (this.body = b());
            q.appendChild(r);
            this.changeBodyEvent.fire(r);
            this.changeContentEvent.fire();
        },
        setFooter: function (r) {
            var q = this.footer || (this.footer = c());
            if (r.nodeName) {
                q.innerHTML = "";
                q.appendChild(r);
            } else {
                q.innerHTML = r;
            }
            if (this._rendered) {
                this._renderFooter();
            }
            this.changeFooterEvent.fire(r);
            this.changeContentEvent.fire();
        },
        appendToFooter: function (r) {
            var q = this.footer || (this.footer = c());
            q.appendChild(r);
            this.changeFooterEvent.fire(r);
            this.changeContentEvent.fire();
        },
        render: function (s, q) {
            var t = this;

            function r(u) {
                if (typeof u == "string") {
                    u = document.getElementById(u);
                }
                if (u) {
                    t._addToParent(u, t.element);
                    t.appendEvent.fire();
                }
            }
            this.beforeRenderEvent.fire();
            if (!q) {
                q = this.element;
            }
            if (s) {
                r(s);
            } else {
                if (!f.inDocument(this.element)) {
                    return false;
                }
            }
            this._renderHeader(q);
            this._renderBody(q);
            this._renderFooter(q);
            this._rendered = true;
            this.renderEvent.fire();
            return true;
        },
        _renderHeader: function (q) {
            q = q || this.element;
            if (this.header && !f.inDocument(this.header)) {
                var r = q.firstChild;
                if (r) {
                    q.insertBefore(this.header, r);
                } else {
                    q.appendChild(this.header);
                }
            }
        },
        _renderBody: function (q) {
            q = q || this.element;
            if (this.body && !f.inDocument(this.body)) {
                if (this.footer && f.isAncestor(q, this.footer)) {
                    q.insertBefore(this.body, this.footer);
                } else {
                    q.appendChild(this.body);
                }
            }
        },
        _renderFooter: function (q) {
            q = q || this.element;
            if (this.footer && !f.inDocument(this.footer)) {
                q.appendChild(this.footer);
            }
        },
        destroy: function (q) {
            var r, s = !(q);
            if (this.element) {
                n.purgeElement(this.element, s);
                r = this.element.parentNode;
            }
            if (r) {
                r.removeChild(this.element);
            }
            this.element = null;
            this.header = null;
            this.body = null;
            this.footer = null;
            g.textResizeEvent.unsubscribe(this.onDomResize, this);
            this.cfg.destroy();
            this.cfg = null;
            this.destroyEvent.fire();
        },
        show: function () {
            this.cfg.setProperty("visible", true);
        },
        hide: function () {
            this.cfg.setProperty("visible", false);
        },
        configVisible: function (r, q, s) {
            var t = q[0];
            if (t) {
                if (this.beforeShowEvent.fire()) {
                    f.setStyle(this.element, "display", "block");
                    this.showEvent.fire();
                }
            } else {
                if (this.beforeHideEvent.fire()) {
                    f.setStyle(this.element, "display", "none");
                    this.hideEvent.fire();
                }
            }
        },
        configEffect: function (r, q, s) {
            this._cachedEffects = (this.cacheEffects) ? this._createEffects(q[0]) : null;
        },
        cacheEffects: true,
        _createEffects: function (t) {
            var q = null,
                u, r, s;
            if (t) {
                if (t instanceof Array) {
                    q = [];
                    u = t.length;
                    for (r = 0; r < u; r++) {
                        s = t[r];
                        if (s.effect) {
                            q[q.length] = s.effect(this, s.duration);
                        }
                    }
                } else {
                    if (t.effect) {
                        q = [t.effect(this, t.duration)];
                    }
                }
            }
            return q;
        },
        configMonitorResize: function (s, r, t) {
            var q = r[0];
            if (q) {
                this.initResizeMonitor();
            } else {
                g.textResizeEvent.unsubscribe(this.onDomResize, this, true);
                this.resizeMonitor = null;
            }
        },
        _addToParent: function (q, r) {
            if (!this.cfg.getProperty("appendtodocumentbody") && q === document.body && q.firstChild) {
                q.insertBefore(r, q.firstChild);
            } else {
                q.appendChild(r);
            }
        },
        toString: function () {
            return "Module " + this.id;
        }
    };
    YAHOO.lang.augmentProto(g, YAHOO.util.EventProvider);
}());
(function () {
    YAHOO.widget.Overlay = function (p, o) {
        YAHOO.widget.Overlay.superclass.constructor.call(this, p, o);
    };
    var i = YAHOO.lang,
        m = YAHOO.util.CustomEvent,
        g = YAHOO.widget.Module,
        n = YAHOO.util.Event,
        f = YAHOO.util.Dom,
        d = YAHOO.util.Config,
        k = YAHOO.env.ua,
        b = YAHOO.widget.Overlay,
        h = "subscribe",
        e = "unsubscribe",
        c = "contained",
        j, a = {
            "BEFORE_MOVE": "beforeMove",
            "MOVE": "move"
        },
        l = {
            "X": {
                key: "x",
                validator: i.isNumber,
                suppressEvent: true,
                supercedes: ["iframe"]
            },
            "Y": {
                key: "y",
                validator: i.isNumber,
                suppressEvent: true,
                supercedes: ["iframe"]
            },
            "XY": {
                key: "xy",
                suppressEvent: true,
                supercedes: ["iframe"]
            },
            "CONTEXT": {
                key: "context",
                suppressEvent: true,
                supercedes: ["iframe"]
            },
            "FIXED_CENTER": {
                key: "fixedcenter",
                value: false,
                supercedes: ["iframe", "visible"]
            },
            "WIDTH": {
                key: "width",
                suppressEvent: true,
                supercedes: ["context", "fixedcenter", "iframe"]
            },
            "HEIGHT": {
                key: "height",
                suppressEvent: true,
                supercedes: ["context", "fixedcenter", "iframe"]
            },
            "AUTO_FILL_HEIGHT": {
                key: "autofillheight",
                supercedes: ["height"],
                value: "body"
            },
            "ZINDEX": {
                key: "zindex",
                value: null
            },
            "CONSTRAIN_TO_VIEWPORT": {
                key: "constraintoviewport",
                value: false,
                validator: i.isBoolean,
                supercedes: ["iframe", "x", "y", "xy"]
            },
            "IFRAME": {
                key: "iframe",
                value: (k.ie == 6 ? true : false),
                validator: i.isBoolean,
                supercedes: ["zindex"]
            },
            "PREVENT_CONTEXT_OVERLAP": {
                key: "preventcontextoverlap",
                value: false,
                validator: i.isBoolean,
                supercedes: ["constraintoviewport"]
            }
        };
    b.IFRAME_SRC = "javascript:false;";
    b.IFRAME_OFFSET = 3;
    b.VIEWPORT_OFFSET = 10;
    b.TOP_LEFT = "tl";
    b.TOP_RIGHT = "tr";
    b.BOTTOM_LEFT = "bl";
    b.BOTTOM_RIGHT = "br";
    b.PREVENT_OVERLAP_X = {
        "tltr": true,
        "blbr": true,
        "brbl": true,
        "trtl": true
    };
    b.PREVENT_OVERLAP_Y = {
        "trbr": true,
        "tlbl": true,
        "bltl": true,
        "brtr": true
    };
    b.CSS_OVERLAY = "yui-overlay";
    b.CSS_HIDDEN = "yui-overlay-hidden";
    b.CSS_IFRAME = "yui-overlay-iframe";
    b.STD_MOD_RE = /^\s*?(body|footer|header)\s*?$/i;
    b.windowScrollEvent = new m("windowScroll");
    b.windowResizeEvent = new m("windowResize");
    b.windowScrollHandler = function (p) {
        var o = n.getTarget(p);
        if (!o || o === window || o === window.document) {
            if (k.ie) {
                if (!window.scrollEnd) {
                    window.scrollEnd = -1;
                }
                clearTimeout(window.scrollEnd);
                window.scrollEnd = setTimeout(function () {
                    b.windowScrollEvent.fire();
                }, 1);
            } else {
                b.windowScrollEvent.fire();
            }
        }
    };
    b.windowResizeHandler = function (o) {
        if (k.ie) {
            if (!window.resizeEnd) {
                window.resizeEnd = -1;
            }
            clearTimeout(window.resizeEnd);
            window.resizeEnd = setTimeout(function () {
                b.windowResizeEvent.fire();
            }, 100);
        } else {
            b.windowResizeEvent.fire();
        }
    };
    b._initialized = null;
    if (b._initialized === null) {
        n.on(window, "scroll", b.windowScrollHandler);
        n.on(window, "resize", b.windowResizeHandler);
        b._initialized = true;
    }
    b._TRIGGER_MAP = {
        "windowScroll": b.windowScrollEvent,
        "windowResize": b.windowResizeEvent,
        "textResize": g.textResizeEvent
    };
    YAHOO.extend(b, g, {
        CONTEXT_TRIGGERS: [],
        init: function (p, o) {
            b.superclass.init.call(this, p);
            this.beforeInitEvent.fire(b);
            f.addClass(this.element, b.CSS_OVERLAY);
            if (o) {
                this.cfg.applyConfig(o, true);
            }
            if (this.platform == "mac" && k.gecko) {
                if (!d.alreadySubscribed(this.showEvent, this.showMacGeckoScrollbars, this)) {
                    this.showEvent.subscribe(this.showMacGeckoScrollbars, this, true);
                }
                if (!d.alreadySubscribed(this.hideEvent, this.hideMacGeckoScrollbars, this)) {
                    this.hideEvent.subscribe(this.hideMacGeckoScrollbars, this, true);
                }
            }
            this.initEvent.fire(b);
        },
        initEvents: function () {
            b.superclass.initEvents.call(this);
            var o = m.LIST;
            this.beforeMoveEvent = this.createEvent(a.BEFORE_MOVE);
            this.beforeMoveEvent.signature = o;
            this.moveEvent = this.createEvent(a.MOVE);
            this.moveEvent.signature = o;
        },
        initDefaultConfig: function () {
            b.superclass.initDefaultConfig.call(this);
            var o = this.cfg;
            o.addProperty(l.X.key, {
                handler: this.configX,
                validator: l.X.validator,
                suppressEvent: l.X.suppressEvent,
                supercedes: l.X.supercedes
            });
            o.addProperty(l.Y.key, {
                handler: this.configY,
                validator: l.Y.validator,
                suppressEvent: l.Y.suppressEvent,
                supercedes: l.Y.supercedes
            });
            o.addProperty(l.XY.key, {
                handler: this.configXY,
                suppressEvent: l.XY.suppressEvent,
                supercedes: l.XY.supercedes
            });
            o.addProperty(l.CONTEXT.key, {
                handler: this.configContext,
                suppressEvent: l.CONTEXT.suppressEvent,
                supercedes: l.CONTEXT.supercedes
            });
            o.addProperty(l.FIXED_CENTER.key, {
                handler: this.configFixedCenter,
                value: l.FIXED_CENTER.value,
                validator: l.FIXED_CENTER.validator,
                supercedes: l.FIXED_CENTER.supercedes
            });
            o.addProperty(l.WIDTH.key, {
                handler: this.configWidth,
                suppressEvent: l.WIDTH.suppressEvent,
                supercedes: l.WIDTH.supercedes
            });
            o.addProperty(l.HEIGHT.key, {
                handler: this.configHeight,
                suppressEvent: l.HEIGHT.suppressEvent,
                supercedes: l.HEIGHT.supercedes
            });
            o.addProperty(l.AUTO_FILL_HEIGHT.key, {
                handler: this.configAutoFillHeight,
                value: l.AUTO_FILL_HEIGHT.value,
                validator: this._validateAutoFill,
                supercedes: l.AUTO_FILL_HEIGHT.supercedes
            });
            o.addProperty(l.ZINDEX.key, {
                handler: this.configzIndex,
                value: l.ZINDEX.value
            });
            o.addProperty(l.CONSTRAIN_TO_VIEWPORT.key, {
                handler: this.configConstrainToViewport,
                value: l.CONSTRAIN_TO_VIEWPORT.value,
                validator: l.CONSTRAIN_TO_VIEWPORT.validator,
                supercedes: l.CONSTRAIN_TO_VIEWPORT.supercedes
            });
            o.addProperty(l.IFRAME.key, {
                handler: this.configIframe,
                value: l.IFRAME.value,
                validator: l.IFRAME.validator,
                supercedes: l.IFRAME.supercedes
            });
            o.addProperty(l.PREVENT_CONTEXT_OVERLAP.key, {
                value: l.PREVENT_CONTEXT_OVERLAP.value,
                validator: l.PREVENT_CONTEXT_OVERLAP.validator,
                supercedes: l.PREVENT_CONTEXT_OVERLAP.supercedes
            });
        },
        moveTo: function (o, p) {
            this.cfg.setProperty("xy", [o, p]);
        },
        hideMacGeckoScrollbars: function () {
            f.replaceClass(this.element, "show-scrollbars", "hide-scrollbars");
        },
        showMacGeckoScrollbars: function () {
            f.replaceClass(this.element, "hide-scrollbars", "show-scrollbars");
        },
        _setDomVisibility: function (o) {
            f.setStyle(this.element, "visibility", (o) ? "visible" : "hidden");
            var p = b.CSS_HIDDEN;
            if (o) {
                f.removeClass(this.element, p);
            } else {
                f.addClass(this.element, p);
            }
        },
        configVisible: function (x, w, t) {
            var p = w[0],
                B = f.getStyle(this.element, "visibility"),
                o = this._cachedEffects || this._createEffects(this.cfg.getProperty("effect")),
                A = (this.platform == "mac" && k.gecko),
                y = d.alreadySubscribed,
                q, v, s, r, u, z;
            if (B == "inherit") {
                v = this.element.parentNode;
                while (v.nodeType != 9 && v.nodeType != 11) {
                    B = f.getStyle(v, "visibility");
                    if (B != "inherit") {
                        break;
                    }
                    v = v.parentNode;
                }
                if (B == "inherit") {
                    B = "visible";
                }
            }
            if (p) {
                if (A) {
                    this.showMacGeckoScrollbars();
                }
                if (o) {
                    if (p) {
                        if (B != "visible" || B === "" || this._fadingOut) {
                            if (this.beforeShowEvent.fire()) {
                                z = o.length;
                                for (s = 0; s < z; s++) {
                                    q = o[s];
                                    if (s === 0 && !y(q.animateInCompleteEvent, this.showEvent.fire, this.showEvent)) {
                                        q.animateInCompleteEvent.subscribe(this.showEvent.fire, this.showEvent, true);
                                    }
                                    q.animateIn();
                                }
                            }
                        }
                    }
                } else {
                    if (B != "visible" || B === "") {
                        if (this.beforeShowEvent.fire()) {
                            this._setDomVisibility(true);
                            this.cfg.refireEvent("iframe");
                            this.showEvent.fire();
                        }
                    } else {
                        this._setDomVisibility(true);
                    }
                }
            } else {
                if (A) {
                    this.hideMacGeckoScrollbars();
                }
                if (o) {
                    if (B == "visible" || this._fadingIn) {
                        if (this.beforeHideEvent.fire()) {
                            z = o.length;
                            for (r = 0; r < z; r++) {
                                u = o[r];
                                if (r === 0 && !y(u.animateOutCompleteEvent, this.hideEvent.fire, this.hideEvent)) {
                                    u.animateOutCompleteEvent.subscribe(this.hideEvent.fire, this.hideEvent, true);
                                }
                                u.animateOut();
                            }
                        }
                    } else {
                        if (B === "") {
                            this._setDomVisibility(false);
                        }
                    }
                } else {
                    if (B == "visible" || B === "") {
                        if (this.beforeHideEvent.fire()) {
                            this._setDomVisibility(false);
                            this.hideEvent.fire();
                        }
                    } else {
                        this._setDomVisibility(false);
                    }
                }
            }
        },
        doCenterOnDOMEvent: function () {
            var o = this.cfg,
                p = o.getProperty("fixedcenter");
            if (o.getProperty("visible")) {
                if (p && (p !== c || this.fitsInViewport())) {
                    this.center();
                }
            }
        },
        fitsInViewport: function () {
            var s = b.VIEWPORT_OFFSET,
                q = this.element,
                t = q.offsetWidth,
                r = q.offsetHeight,
                o = f.getViewportWidth(),
                p = f.getViewportHeight();
            return ((t + s < o) && (r + s < p));
        },
        configFixedCenter: function (s, q, t) {
            var u = q[0],
                p = d.alreadySubscribed,
                r = b.windowResizeEvent,
                o = b.windowScrollEvent;
            if (u) {
                this.center();
                if (!p(this.beforeShowEvent, this.center)) {
                    this.beforeShowEvent.subscribe(this.center);
                }
                if (!p(r, this.doCenterOnDOMEvent, this)) {
                    r.subscribe(this.doCenterOnDOMEvent, this, true);
                }
                if (!p(o, this.doCenterOnDOMEvent, this)) {
                    o.subscribe(this.doCenterOnDOMEvent, this, true);
                }
            } else {
                this.beforeShowEvent.unsubscribe(this.center);
                r.unsubscribe(this.doCenterOnDOMEvent, this);
                o.unsubscribe(this.doCenterOnDOMEvent, this);
            }
        },
        configHeight: function (r, p, s) {
            var o = p[0],
                q = this.element;
            f.setStyle(q, "height", o);
            this.cfg.refireEvent("iframe");
        },
        configAutoFillHeight: function (t, s, p) {
            var v = s[0],
                q = this.cfg,
                u = "autofillheight",
                w = "height",
                r = q.getProperty(u),
                o = this._autoFillOnHeightChange;
            q.unsubscribeFromConfigEvent(w, o);
            g.textResizeEvent.unsubscribe(o);
            this.changeContentEvent.unsubscribe(o);
            if (r && v !== r && this[r]) {
                f.setStyle(this[r], w, "");
            }
            if (v) {
                v = i.trim(v.toLowerCase());
                q.subscribeToConfigEvent(w, o, this[v], this);
                g.textResizeEvent.subscribe(o, this[v], this);
                this.changeContentEvent.subscribe(o, this[v], this);
                q.setProperty(u, v, true);
            }
        },
        configWidth: function (r, o, s) {
            var q = o[0],
                p = this.element;
            f.setStyle(p, "width", q);
            this.cfg.refireEvent("iframe");
        },
        configzIndex: function (q, o, r) {
            var s = o[0],
                p = this.element;
            if (!s) {
                s = f.getStyle(p, "zIndex");
                if (!s || isNaN(s)) {
                    s = 0;
                }
            }
            if (this.iframe || this.cfg.getProperty("iframe") === true) {
                if (s <= 0) {
                    s = 1;
                }
            }
            f.setStyle(p, "zIndex", s);
            this.cfg.setProperty("zIndex", s, true);
            if (this.iframe) {
                this.stackIframe();
            }
        },
        configXY: function (q, p, r) {
            var t = p[0],
                o = t[0],
                s = t[1];
            this.cfg.setProperty("x", o);
            this.cfg.setProperty("y", s);
            this.beforeMoveEvent.fire([o, s]);
            o = this.cfg.getProperty("x");
            s = this.cfg.getProperty("y");
            this.cfg.refireEvent("iframe");
            this.moveEvent.fire([o, s]);
        },
        configX: function (q, p, r) {
            var o = p[0],
                s = this.cfg.getProperty("y");
            this.cfg.setProperty("x", o, true);
            this.cfg.setProperty("y", s, true);
            this.beforeMoveEvent.fire([o, s]);
            o = this.cfg.getProperty("x");
            s = this.cfg.getProperty("y");
            f.setX(this.element, o, true);
            this.cfg.setProperty("xy", [o, s], true);
            this.cfg.refireEvent("iframe");
            this.moveEvent.fire([o, s]);
        },
        configY: function (q, p, r) {
            var o = this.cfg.getProperty("x"),
                s = p[0];
            this.cfg.setProperty("x", o, true);
            this.cfg.setProperty("y", s, true);
            this.beforeMoveEvent.fire([o, s]);
            o = this.cfg.getProperty("x");
            s = this.cfg.getProperty("y");
            f.setY(this.element, s, true);
            this.cfg.setProperty("xy", [o, s], true);
            this.cfg.refireEvent("iframe");
            this.moveEvent.fire([o, s]);
        },
        showIframe: function () {
            var p = this.iframe,
                o;
            if (p) {
                o = this.element.parentNode;
                if (o != p.parentNode) {
                    this._addToParent(o, p);
                }
                p.style.display = "block";
            }
        },
        hideIframe: function () {
            if (this.iframe) {
                this.iframe.style.display = "none";
            }
        },
        syncIframe: function () {
            var o = this.iframe,
                q = this.element,
                s = b.IFRAME_OFFSET,
                p = (s * 2),
                r;
            if (o) {
                o.style.width = (q.offsetWidth + p + "px");
                o.style.height = (q.offsetHeight + p + "px");
                r = this.cfg.getProperty("xy");
                if (!i.isArray(r) || (isNaN(r[0]) || isNaN(r[1]))) {
                    this.syncPosition();
                    r = this.cfg.getProperty("xy");
                }
                f.setXY(o, [(r[0] - s), (r[1] - s)]);
            }
        },
        stackIframe: function () {
            if (this.iframe) {
                var o = f.getStyle(this.element, "zIndex");
                if (!YAHOO.lang.isUndefined(o) && !isNaN(o)) {
                    f.setStyle(this.iframe, "zIndex", (o - 1));
                }
            }
        },
        configIframe: function (r, q, s) {
            var o = q[0];

            function t() {
                var v = this.iframe,
                    w = this.element,
                    x;
                if (!v) {
                    if (!j) {
                        j = document.createElement("iframe");
                        if (this.isSecure) {
                            j.src = b.IFRAME_SRC;
                        }
                        if (k.ie) {
                            j.style.filter = "alpha(opacity=0)";
                            j.frameBorder = 0;
                        } else {
                            j.style.opacity = "0";
                        }
                        j.style.position = "absolute";
                        j.style.border = "none";
                        j.style.margin = "0";
                        j.style.padding = "0";
                        j.style.display = "none";
                        j.tabIndex = -1;
                        j.className = b.CSS_IFRAME;
                    }
                    v = j.cloneNode(false);
                    v.id = this.id + "_f";
                    x = w.parentNode;
                    var u = x || document.body;
                    this._addToParent(u, v);
                    this.iframe = v;
                }
                this.showIframe();
                this.syncIframe();
                this.stackIframe();
                if (!this._hasIframeEventListeners) {
                    this.showEvent.subscribe(this.showIframe);
                    this.hideEvent.subscribe(this.hideIframe);
                    this.changeContentEvent.subscribe(this.syncIframe);
                    this._hasIframeEventListeners = true;
                }
            }
            function p() {
                t.call(this);
                this.beforeShowEvent.unsubscribe(p);
                this._iframeDeferred = false;
            }
            if (o) {
                if (this.cfg.getProperty("visible")) {
                    t.call(this);
                } else {
                    if (!this._iframeDeferred) {
                        this.beforeShowEvent.subscribe(p);
                        this._iframeDeferred = true;
                    }
                }
            } else {
                this.hideIframe();
                if (this._hasIframeEventListeners) {
                    this.showEvent.unsubscribe(this.showIframe);
                    this.hideEvent.unsubscribe(this.hideIframe);
                    this.changeContentEvent.unsubscribe(this.syncIframe);
                    this._hasIframeEventListeners = false;
                }
            }
        },
        _primeXYFromDOM: function () {
            if (YAHOO.lang.isUndefined(this.cfg.getProperty("xy"))) {
                this.syncPosition();
                this.cfg.refireEvent("xy");
                this.beforeShowEvent.unsubscribe(this._primeXYFromDOM);
            }
        },
        configConstrainToViewport: function (p, o, q) {
            var r = o[0];
            if (r) {
                if (!d.alreadySubscribed(this.beforeMoveEvent, this.enforceConstraints, this)) {
                    this.beforeMoveEvent.subscribe(this.enforceConstraints, this, true);
                }
                if (!d.alreadySubscribed(this.beforeShowEvent, this._primeXYFromDOM)) {
                    this.beforeShowEvent.subscribe(this._primeXYFromDOM);
                }
            } else {
                this.beforeShowEvent.unsubscribe(this._primeXYFromDOM);
                this.beforeMoveEvent.unsubscribe(this.enforceConstraints, this);
            }
        },
        configContext: function (u, t, q) {
            var x = t[0],
                r, o, v, s, p, w = this.CONTEXT_TRIGGERS;
            if (x) {
                r = x[0];
                o = x[1];
                v = x[2];
                s = x[3];
                p = x[4];
                if (w && w.length > 0) {
                    s = (s || []).concat(w);
                }
                if (r) {
                    if (typeof r == "string") {
                        this.cfg.setProperty("context", [document.getElementById(r), o, v, s, p], true);
                    }
                    if (o && v) {
                        this.align(o, v, p);
                    }
                    if (this._contextTriggers) {
                        this._processTriggers(this._contextTriggers, e, this._alignOnTrigger);
                    }
                    if (s) {
                        this._processTriggers(s, h, this._alignOnTrigger);
                        this._contextTriggers = s;
                    }
                }
            }
        },
        _alignOnTrigger: function (p, o) {
            this.align();
        },
        _findTriggerCE: function (o) {
            var p = null;
            if (o instanceof m) {
                p = o;
            } else {
                if (b._TRIGGER_MAP[o]) {
                    p = b._TRIGGER_MAP[o];
                }
            }
            return p;
        },
        _processTriggers: function (s, v, r) {
            var q, u;
            for (var p = 0, o = s.length; p < o; ++p) {
                q = s[p];
                u = this._findTriggerCE(q);
                if (u) {
                    u[v](r, this, true);
                } else {
                    this[v](q, r);
                }
            }
        },
        align: function (p, w, s) {
            var v = this.cfg.getProperty("context"),
                t = this,
                o, q, u;

            function r(z, A) {
                var y = null,
                    x = null;
                switch (p) {
                case b.TOP_LEFT:
                    y = A;
                    x = z;
                    break;
                case b.TOP_RIGHT:
                    y = A - q.offsetWidth;
                    x = z;
                    break;
                case b.BOTTOM_LEFT:
                    y = A;
                    x = z - q.offsetHeight;
                    break;
                case b.BOTTOM_RIGHT:
                    y = A - q.offsetWidth;
                    x = z - q.offsetHeight;
                    break;
                }
                if (y !== null && x !== null) {
                    if (s) {
                        y += s[0];
                        x += s[1];
                    }
                    t.moveTo(y, x);
                }
            }
            if (v) {
                o = v[0];
                q = this.element;
                t = this;
                if (!p) {
                    p = v[1];
                }
                if (!w) {
                    w = v[2];
                }
                if (!s && v[4]) {
                    s = v[4];
                }
                if (q && o) {
                    u = f.getRegion(o);
                    switch (w) {
                    case b.TOP_LEFT:
                        r(u.top, u.left);
                        break;
                    case b.TOP_RIGHT:
                        r(u.top, u.right);
                        break;
                    case b.BOTTOM_LEFT:
                        r(u.bottom, u.left);
                        break;
                    case b.BOTTOM_RIGHT:
                        r(u.bottom, u.right);
                        break;
                    }
                }
            }
        },
        enforceConstraints: function (p, o, q) {
            var s = o[0];
            var r = this.getConstrainedXY(s[0], s[1]);
            this.cfg.setProperty("x", r[0], true);
            this.cfg.setProperty("y", r[1], true);
            this.cfg.setProperty("xy", r, true);
        },
        _getConstrainedPos: function (y, p) {
            var t = this.element,
                r = b.VIEWPORT_OFFSET,
                A = (y == "x"),
                z = (A) ? t.offsetWidth : t.offsetHeight,
                s = (A) ? f.getViewportWidth() : f.getViewportHeight(),
                D = (A) ? f.getDocumentScrollLeft() : f.getDocumentScrollTop(),
                C = (A) ? b.PREVENT_OVERLAP_X : b.PREVENT_OVERLAP_Y,
                o = this.cfg.getProperty("context"),
                u = (z + r < s),
                w = this.cfg.getProperty("preventcontextoverlap") && o && C[(o[1] + o[2])],
                v = D + r,
                B = D + s - z - r,
                q = p;
            if (p < v || p > B) {
                if (w) {
                    q = this._preventOverlap(y, o[0], z, s, D);
                } else {
                    if (u) {
                        if (p < v) {
                            q = v;
                        } else {
                            if (p > B) {
                                q = B;
                            }
                        }
                    } else {
                        q = v;
                    }
                }
            }
            return q;
        },
        _preventOverlap: function (y, w, z, u, C) {
            var A = (y == "x"),
                t = b.VIEWPORT_OFFSET,
                s = this,
                q = ((A) ? f.getX(w) : f.getY(w)) - C,
                o = (A) ? w.offsetWidth : w.offsetHeight,
                p = q - t,
                r = (u - (q + o)) - t,
                D = false,
                v = function () {
                    var x;
                    if ((s.cfg.getProperty(y) - C) > q) {
                        x = (q - z);
                    } else {
                        x = (q + o);
                    }
                    s.cfg.setProperty(y, (x + C), true);
                    return x;
                },
                B = function () {
                    var E = ((s.cfg.getProperty(y) - C) > q) ? r : p,
                        x;
                    if (z > E) {
                        if (D) {
                            v();
                        } else {
                            v();
                            D = true;
                            x = B();
                        }
                    }
                    return x;
                };
            B();
            return this.cfg.getProperty(y);
        },
        getConstrainedX: function (o) {
            return this._getConstrainedPos("x", o);
        },
        getConstrainedY: function (o) {
            return this._getConstrainedPos("y", o);
        },
        getConstrainedXY: function (o, p) {
            return [this.getConstrainedX(o), this.getConstrainedY(p)];
        },
        center: function () {
            var r = b.VIEWPORT_OFFSET,
                s = this.element.offsetWidth,
                q = this.element.offsetHeight,
                p = f.getViewportWidth(),
                t = f.getViewportHeight(),
                o, u;
            if (s < p) {
                o = (p / 2) - (s / 2) + f.getDocumentScrollLeft();
            } else {
                o = r + f.getDocumentScrollLeft();
            }
            if (q < t) {
                u = (t / 2) - (q / 2) + f.getDocumentScrollTop();
            } else {
                u = r + f.getDocumentScrollTop();
            }
            this.cfg.setProperty("xy", [parseInt(o, 10), parseInt(u, 10)]);
            this.cfg.refireEvent("iframe");
            if (k.webkit) {
                this.forceContainerRedraw();
            }
        },
        syncPosition: function () {
            var o = f.getXY(this.element);
            this.cfg.setProperty("x", o[0], true);
            this.cfg.setProperty("y", o[1], true);
            this.cfg.setProperty("xy", o, true);
        },
        onDomResize: function (q, p) {
            var o = this;
            b.superclass.onDomResize.call(this, q, p);
            setTimeout(function () {
                o.syncPosition();
                o.cfg.refireEvent("iframe");
                o.cfg.refireEvent("context");
            }, 0);
        },
        _getComputedHeight: (function () {
            if (document.defaultView && document.defaultView.getComputedStyle) {
                return function (p) {
                    var o = null;
                    if (p.ownerDocument && p.ownerDocument.defaultView) {
                        var q = p.ownerDocument.defaultView.getComputedStyle(p, "");
                        if (q) {
                            o = parseInt(q.height, 10);
                        }
                    }
                    return (i.isNumber(o)) ? o : null;
                };
            } else {
                return function (p) {
                    var o = null;
                    if (p.style.pixelHeight) {
                        o = p.style.pixelHeight;
                    }
                    return (i.isNumber(o)) ? o : null;
                };
            }
        })(),
        _validateAutoFillHeight: function (o) {
            return (!o) || (i.isString(o) && b.STD_MOD_RE.test(o));
        },
        _autoFillOnHeightChange: function (r, p, q) {
            var o = this.cfg.getProperty("height");
            if ((o && o !== "auto") || (o === 0)) {
                this.fillHeight(q);
            }
        },
        _getPreciseHeight: function (p) {
            var o = p.offsetHeight;
            if (p.getBoundingClientRect) {
                var q = p.getBoundingClientRect();
                o = q.bottom - q.top;
            }
            return o;
        },
        fillHeight: function (r) {
            if (r) {
                var p = this.innerElement || this.element,
                    o = [this.header, this.body, this.footer],
                    v, w = 0,
                    x = 0,
                    t = 0,
                    q = false;
                for (var u = 0, s = o.length; u < s; u++) {
                    v = o[u];
                    if (v) {
                        if (r !== v) {
                            x += this._getPreciseHeight(v);
                        } else {
                            q = true;
                        }
                    }
                }
                if (q) {
                    if (k.ie || k.opera) {
                        f.setStyle(r, "height", 0 + "px");
                    }
                    w = this._getComputedHeight(p);
                    if (w === null) {
                        f.addClass(p, "yui-override-padding");
                        w = p.clientHeight;
                        f.removeClass(p, "yui-override-padding");
                    }
                    t = Math.max(w - x, 0);
                    f.setStyle(r, "height", t + "px");
                    if (r.offsetHeight != t) {
                        t = Math.max(t - (r.offsetHeight - t), 0);
                    }
                    f.setStyle(r, "height", t + "px");
                }
            }
        },
        bringToTop: function () {
            var s = [],
                r = this.element;

            function v(z, y) {
                var B = f.getStyle(z, "zIndex"),
                    A = f.getStyle(y, "zIndex"),
                    x = (!B || isNaN(B)) ? 0 : parseInt(B, 10),
                    w = (!A || isNaN(A)) ? 0 : parseInt(A, 10);
                if (x > w) {
                    return -1;
                } else {
                    if (x < w) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
            function q(y) {
                var x = f.hasClass(y, b.CSS_OVERLAY),
                    w = YAHOO.widget.Panel;
                if (x && !f.isAncestor(r, y)) {
                    if (w && f.hasClass(y, w.CSS_PANEL)) {
                        s[s.length] = y.parentNode;
                    } else {
                        s[s.length] = y;
                    }
                }
            }
            f.getElementsBy(q, "div", document.body);
            s.sort(v);
            var o = s[0],
                u;
            if (o) {
                u = f.getStyle(o, "zIndex");
                if (!isNaN(u)) {
                    var t = false;
                    if (o != r) {
                        t = true;
                    } else {
                        if (s.length > 1) {
                            var p = f.getStyle(s[1], "zIndex");
                            if (!isNaN(p) && (u == p)) {
                                t = true;
                            }
                        }
                    }
                    if (t) {
                        this.cfg.setProperty("zindex", (parseInt(u, 10) + 2));
                    }
                }
            }
        },
        destroy: function (o) {
            if (this.iframe) {
                this.iframe.parentNode.removeChild(this.iframe);
            }
            this.iframe = null;
            b.windowResizeEvent.unsubscribe(this.doCenterOnDOMEvent, this);
            b.windowScrollEvent.unsubscribe(this.doCenterOnDOMEvent, this);
            g.textResizeEvent.unsubscribe(this._autoFillOnHeightChange);
            if (this._contextTriggers) {
                this._processTriggers(this._contextTriggers, e, this._alignOnTrigger);
            }
            b.superclass.destroy.call(this, o);
        },
        forceContainerRedraw: function () {
            var o = this;
            f.addClass(o.element, "yui-force-redraw");
            setTimeout(function () {
                f.removeClass(o.element, "yui-force-redraw");
            }, 0);
        },
        toString: function () {
            return "Overlay " + this.id;
        }
    });
}());
(function () {
    YAHOO.widget.OverlayManager = function (g) {
        this.init(g);
    };
    var d = YAHOO.widget.Overlay,
        c = YAHOO.util.Event,
        e = YAHOO.util.Dom,
        b = YAHOO.util.Config,
        f = YAHOO.util.CustomEvent,
        a = YAHOO.widget.OverlayManager;
    a.CSS_FOCUSED = "focused";
    a.prototype = {
        constructor: a,
        overlays: null,
        initDefaultConfig: function () {
            this.cfg.addProperty("overlays", {
                suppressEvent: true
            });
            this.cfg.addProperty("focusevent", {
                value: "mousedown"
            });
        },
        init: function (i) {
            this.cfg = new b(this);
            this.initDefaultConfig();
            if (i) {
                this.cfg.applyConfig(i, true);
            }
            this.cfg.fireQueue();
            var h = null;
            this.getActive = function () {
                return h;
            };
            this.focus = function (j) {
                var k = this.find(j);
                if (k) {
                    k.focus();
                }
            };
            this.remove = function (k) {
                var m = this.find(k),
                    j;
                if (m) {
                    if (h == m) {
                        h = null;
                    }
                    var l = (m.element === null && m.cfg === null) ? true : false;
                    if (!l) {
                        j = e.getStyle(m.element, "zIndex");
                        m.cfg.setProperty("zIndex", -1000, true);
                    }
                    this.overlays.sort(this.compareZIndexDesc);
                    this.overlays = this.overlays.slice(0, (this.overlays.length - 1));
                    m.hideEvent.unsubscribe(m.blur);
                    m.destroyEvent.unsubscribe(this._onOverlayDestroy, m);
                    m.focusEvent.unsubscribe(this._onOverlayFocusHandler, m);
                    m.blurEvent.unsubscribe(this._onOverlayBlurHandler, m);
                    if (!l) {
                        c.removeListener(m.element, this.cfg.getProperty("focusevent"), this._onOverlayElementFocus);
                        m.cfg.setProperty("zIndex", j, true);
                        m.cfg.setProperty("manager", null);
                    }
                    if (m.focusEvent._managed) {
                        m.focusEvent = null;
                    }
                    if (m.blurEvent._managed) {
                        m.blurEvent = null;
                    }
                    if (m.focus._managed) {
                        m.focus = null;
                    }
                    if (m.blur._managed) {
                        m.blur = null;
                    }
                }
            };
            this.blurAll = function () {
                var k = this.overlays.length,
                    j;
                if (k > 0) {
                    j = k - 1;
                    do {
                        this.overlays[j].blur();
                    } while (j--);
                }
            };
            this._manageBlur = function (j) {
                var k = false;
                if (h == j) {
                    e.removeClass(h.element, a.CSS_FOCUSED);
                    h = null;
                    k = true;
                }
                return k;
            };
            this._manageFocus = function (j) {
                var k = false;
                if (h != j) {
                    if (h) {
                        h.blur();
                    }
                    h = j;
                    this.bringToTop(h);
                    e.addClass(h.element, a.CSS_FOCUSED);
                    k = true;
                }
                return k;
            };
            var g = this.cfg.getProperty("overlays");
            if (!this.overlays) {
                this.overlays = [];
            }
            if (g) {
                this.register(g);
                this.overlays.sort(this.compareZIndexDesc);
            }
        },
        _onOverlayElementFocus: function (i) {
            var g = c.getTarget(i),
                h = this.close;
            if (h && (g == h || e.isAncestor(h, g))) {
                this.blur();
            } else {
                this.focus();
            }
        },
        _onOverlayDestroy: function (h, g, i) {
            this.remove(i);
        },
        _onOverlayFocusHandler: function (h, g, i) {
            this._manageFocus(i);
        },
        _onOverlayBlurHandler: function (h, g, i) {
            this._manageBlur(i);
        },
        _bindFocus: function (g) {
            var h = this;
            if (!g.focusEvent) {
                g.focusEvent = g.createEvent("focus");
                g.focusEvent.signature = f.LIST;
                g.focusEvent._managed = true;
            } else {
                g.focusEvent.subscribe(h._onOverlayFocusHandler, g, h);
            }
            if (!g.focus) {
                c.on(g.element, h.cfg.getProperty("focusevent"), h._onOverlayElementFocus, null, g);
                g.focus = function () {
                    if (h._manageFocus(this)) {
                        if (this.cfg.getProperty("visible") && this.focusFirst) {
                            this.focusFirst();
                        }
                        this.focusEvent.fire();
                    }
                };
                g.focus._managed = true;
            }
        },
        _bindBlur: function (g) {
            var h = this;
            if (!g.blurEvent) {
                g.blurEvent = g.createEvent("blur");
                g.blurEvent.signature = f.LIST;
                g.focusEvent._managed = true;
            } else {
                g.blurEvent.subscribe(h._onOverlayBlurHandler, g, h);
            }
            if (!g.blur) {
                g.blur = function () {
                    if (h._manageBlur(this)) {
                        this.blurEvent.fire();
                    }
                };
                g.blur._managed = true;
            }
            g.hideEvent.subscribe(g.blur);
        },
        _bindDestroy: function (g) {
            var h = this;
            g.destroyEvent.subscribe(h._onOverlayDestroy, g, h);
        },
        _syncZIndex: function (g) {
            var h = e.getStyle(g.element, "zIndex");
            if (!isNaN(h)) {
                g.cfg.setProperty("zIndex", parseInt(h, 10));
            } else {
                g.cfg.setProperty("zIndex", 0);
            }
        },
        register: function (g) {
            var k = false,
                h, j;
            if (g instanceof d) {
                g.cfg.addProperty("manager", {
                    value: this
                });
                this._bindFocus(g);
                this._bindBlur(g);
                this._bindDestroy(g);
                this._syncZIndex(g);
                this.overlays.push(g);
                this.bringToTop(g);
                k = true;
            } else {
                if (g instanceof Array) {
                    for (h = 0, j = g.length; h < j; h++) {
                        k = this.register(g[h]) || k;
                    }
                }
            }
            return k;
        },
        bringToTop: function (m) {
            var i = this.find(m),
                l, g, j;
            if (i) {
                j = this.overlays;
                j.sort(this.compareZIndexDesc);
                g = j[0];
                if (g) {
                    l = e.getStyle(g.element, "zIndex");
                    if (!isNaN(l)) {
                        var k = false;
                        if (g !== i) {
                            k = true;
                        } else {
                            if (j.length > 1) {
                                var h = e.getStyle(j[1].element, "zIndex");
                                if (!isNaN(h) && (l == h)) {
                                    k = true;
                                }
                            }
                        }
                        if (k) {
                            i.cfg.setProperty("zindex", (parseInt(l, 10) + 2));
                        }
                    }
                    j.sort(this.compareZIndexDesc);
                }
            }
        },
        find: function (g) {
            var l = g instanceof d,
                j = this.overlays,
                p = j.length,
                k = null,
                m, h;
            if (l || typeof g == "string") {
                for (h = p - 1; h >= 0; h--) {
                    m = j[h];
                    if ((l && (m === g)) || (m.id == g)) {
                        k = m;
                        break;
                    }
                }
            }
            return k;
        },
        compareZIndexDesc: function (j, i) {
            var h = (j.cfg) ? j.cfg.getProperty("zIndex") : null,
                g = (i.cfg) ? i.cfg.getProperty("zIndex") : null;
            if (h === null && g === null) {
                return 0;
            } else {
                if (h === null) {
                    return 1;
                } else {
                    if (g === null) {
                        return -1;
                    } else {
                        if (h > g) {
                            return -1;
                        } else {
                            if (h < g) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            }
        },
        showAll: function () {
            var h = this.overlays,
                j = h.length,
                g;
            for (g = j - 1; g >= 0; g--) {
                h[g].show();
            }
        },
        hideAll: function () {
            var h = this.overlays,
                j = h.length,
                g;
            for (g = j - 1; g >= 0; g--) {
                h[g].hide();
            }
        },
        toString: function () {
            return "OverlayManager";
        }
    };
}());
(function () {
    YAHOO.widget.Tooltip = function (p, o) {
        YAHOO.widget.Tooltip.superclass.constructor.call(this, p, o);
    };
    var e = YAHOO.lang,
        n = YAHOO.util.Event,
        m = YAHOO.util.CustomEvent,
        c = YAHOO.util.Dom,
        j = YAHOO.widget.Tooltip,
        h = YAHOO.env.ua,
        g = (h.ie && (h.ie <= 6 || document.compatMode == "BackCompat")),
        f, i = {
            "PREVENT_OVERLAP": {
                key: "preventoverlap",
                value: true,
                validator: e.isBoolean,
                supercedes: ["x", "y", "xy"]
            },
            "SHOW_DELAY": {
                key: "showdelay",
                value: 200,
                validator: e.isNumber
            },
            "AUTO_DISMISS_DELAY": {
                key: "autodismissdelay",
                value: 5000,
                validator: e.isNumber
            },
            "HIDE_DELAY": {
                key: "hidedelay",
                value: 250,
                validator: e.isNumber
            },
            "TEXT": {
                key: "text",
                suppressEvent: true
            },
            "CONTAINER": {
                key: "container"
            },
            "DISABLED": {
                key: "disabled",
                value: false,
                suppressEvent: true
            },
            "XY_OFFSET": {
                key: "xyoffset",
                value: [0, 25],
                suppressEvent: true
            }
        },
        a = {
            "CONTEXT_MOUSE_OVER": "contextMouseOver",
            "CONTEXT_MOUSE_OUT": "contextMouseOut",
            "CONTEXT_TRIGGER": "contextTrigger"
        };
    j.CSS_TOOLTIP = "yui-tt";

    function k(q, o) {
        var p = this.cfg,
            r = p.getProperty("width");
        if (r == o) {
            p.setProperty("width", q);
        }
    }
    function d(p, o) {
        if ("_originalWidth" in this) {
            k.call(this, this._originalWidth, this._forcedWidth);
        }
        var q = document.body,
            u = this.cfg,
            t = u.getProperty("width"),
            r, s;
        if ((!t || t == "auto") && (u.getProperty("container") != q || u.getProperty("x") >= c.getViewportWidth() || u.getProperty("y") >= c.getViewportHeight())) {
            s = this.element.cloneNode(true);
            s.style.visibility = "hidden";
            s.style.top = "0px";
            s.style.left = "0px";
            q.appendChild(s);
            r = (s.offsetWidth + "px");
            q.removeChild(s);
            s = null;
            u.setProperty("width", r);
            u.refireEvent("xy");
            this._originalWidth = t || "";
            this._forcedWidth = r;
        }
    }
    function b(p, o, q) {
        this.render(q);
    }
    function l() {
        n.onDOMReady(b, this.cfg.getProperty("container"), this);
    }
    YAHOO.extend(j, YAHOO.widget.Overlay, {
        init: function (p, o) {
            j.superclass.init.call(this, p);
            this.beforeInitEvent.fire(j);
            c.addClass(this.element, j.CSS_TOOLTIP);
            if (o) {
                this.cfg.applyConfig(o, true);
            }
            this.cfg.queueProperty("visible", false);
            this.cfg.queueProperty("constraintoviewport", true);
            this.setBody("");
            this.subscribe("changeContent", d);
            this.subscribe("init", l);
            this.subscribe("render", this.onRender);
            this.initEvent.fire(j);
        },
        initEvents: function () {
            j.superclass.initEvents.call(this);
            var o = m.LIST;
            this.contextMouseOverEvent = this.createEvent(a.CONTEXT_MOUSE_OVER);
            this.contextMouseOverEvent.signature = o;
            this.contextMouseOutEvent = this.createEvent(a.CONTEXT_MOUSE_OUT);
            this.contextMouseOutEvent.signature = o;
            this.contextTriggerEvent = this.createEvent(a.CONTEXT_TRIGGER);
            this.contextTriggerEvent.signature = o;
        },
        initDefaultConfig: function () {
            j.superclass.initDefaultConfig.call(this);
            this.cfg.addProperty(i.PREVENT_OVERLAP.key, {
                value: i.PREVENT_OVERLAP.value,
                validator: i.PREVENT_OVERLAP.validator,
                supercedes: i.PREVENT_OVERLAP.supercedes
            });
            this.cfg.addProperty(i.SHOW_DELAY.key, {
                handler: this.configShowDelay,
                value: 200,
                validator: i.SHOW_DELAY.validator
            });
            this.cfg.addProperty(i.AUTO_DISMISS_DELAY.key, {
                handler: this.configAutoDismissDelay,
                value: i.AUTO_DISMISS_DELAY.value,
                validator: i.AUTO_DISMISS_DELAY.validator
            });
            this.cfg.addProperty(i.HIDE_DELAY.key, {
                handler: this.configHideDelay,
                value: i.HIDE_DELAY.value,
                validator: i.HIDE_DELAY.validator
            });
            this.cfg.addProperty(i.TEXT.key, {
                handler: this.configText,
                suppressEvent: i.TEXT.suppressEvent
            });
            this.cfg.addProperty(i.CONTAINER.key, {
                handler: this.configContainer,
                value: document.body
            });
            this.cfg.addProperty(i.DISABLED.key, {
                handler: this.configContainer,
                value: i.DISABLED.value,
                supressEvent: i.DISABLED.suppressEvent
            });
            this.cfg.addProperty(i.XY_OFFSET.key, {
                value: i.XY_OFFSET.value.concat(),
                supressEvent: i.XY_OFFSET.suppressEvent
            });
        },
        configText: function (p, o, q) {
            var r = o[0];
            if (r) {
                this.setBody(r);
            }
        },
        configContainer: function (q, p, r) {
            var o = p[0];
            if (typeof o == "string") {
                this.cfg.setProperty("container", document.getElementById(o), true);
            }
        },
        _removeEventListeners: function () {
            var r = this._context,
                o, q, p;
            if (r) {
                o = r.length;
                if (o > 0) {
                    p = o - 1;
                    do {
                        q = r[p];
                        n.removeListener(q, "mouseover", this.onContextMouseOver);
                        n.removeListener(q, "mousemove", this.onContextMouseMove);
                        n.removeListener(q, "mouseout", this.onContextMouseOut);
                    } while (p--);
                }
            }
        },
        configContext: function (t, p, u) {
            var s = p[0],
                v, o, r, q;
            if (s) {
                if (!(s instanceof Array)) {
                    if (typeof s == "string") {
                        this.cfg.setProperty("context", [document.getElementById(s)], true);
                    } else {
                        this.cfg.setProperty("context", [s], true);
                    }
                    s = this.cfg.getProperty("context");
                }
                this._removeEventListeners();
                this._context = s;
                v = this._context;
                if (v) {
                    o = v.length;
                    if (o > 0) {
                        q = o - 1;
                        do {
                            r = v[q];
                            n.on(r, "mouseover", this.onContextMouseOver, this);
                            n.on(r, "mousemove", this.onContextMouseMove, this);
                            n.on(r, "mouseout", this.onContextMouseOut, this);
                        } while (q--);
                    }
                }
            }
        },
        onContextMouseMove: function (p, o) {
            o.pageX = n.getPageX(p);
            o.pageY = n.getPageY(p);
        },
        onContextMouseOver: function (q, p) {
            var o = this;
            if (o.title) {
                p._tempTitle = o.title;
                o.title = "";
            }
            if (p.fireEvent("contextMouseOver", o, q) !== false && !p.cfg.getProperty("disabled")) {
                if (p.hideProcId) {
                    clearTimeout(p.hideProcId);
                    p.hideProcId = null;
                }
                n.on(o, "mousemove", p.onContextMouseMove, p);
                p.showProcId = p.doShow(q, o);
            }
        },
        onContextMouseOut: function (q, p) {
            var o = this;
            if (p._tempTitle) {
                o.title = p._tempTitle;
                p._tempTitle = null;
            }
            if (p.showProcId) {
                clearTimeout(p.showProcId);
                p.showProcId = null;
            }
            if (p.hideProcId) {
                clearTimeout(p.hideProcId);
                p.hideProcId = null;
            }
            p.fireEvent("contextMouseOut", o, q);
            p.hideProcId = setTimeout(function () {
                p.hide();
            }, p.cfg.getProperty("hidedelay"));
        },
        doShow: function (r, o) {
            var t = this.cfg.getProperty("xyoffset"),
                p = t[0],
                s = t[1],
                q = this;
            if (h.opera && o.tagName && o.tagName.toUpperCase() == "A") {
                s += 12;
            }
            return setTimeout(function () {
                var u = q.cfg.getProperty("text");
                if (q._tempTitle && (u === "" || YAHOO.lang.isUndefined(u) || YAHOO.lang.isNull(u))) {
                    q.setBody(q._tempTitle);
                } else {
                    q.cfg.refireEvent("text");
                }
                q.moveTo(q.pageX + p, q.pageY + s);
                if (q.cfg.getProperty("preventoverlap")) {
                    q.preventOverlap(q.pageX, q.pageY);
                }
                n.removeListener(o, "mousemove", q.onContextMouseMove);
                q.contextTriggerEvent.fire(o);
                q.show();
                q.hideProcId = q.doHide();
            }, this.cfg.getProperty("showdelay"));
        },
        doHide: function () {
            var o = this;
            return setTimeout(function () {
                o.hide();
            }, this.cfg.getProperty("autodismissdelay"));
        },
        preventOverlap: function (s, r) {
            var o = this.element.offsetHeight,
                q = new YAHOO.util.Point(s, r),
                p = c.getRegion(this.element);
            p.top -= 5;
            p.left -= 5;
            p.right += 5;
            p.bottom += 5;
            if (p.contains(q)) {
                this.cfg.setProperty("y", (r - o - 5));
            }
        },
        onRender: function (s, r) {
            function t() {
                var w = this.element,
                    v = this.underlay;
                if (v) {
                    v.style.width = (w.offsetWidth + 6) + "px";
                    v.style.height = (w.offsetHeight + 1) + "px";
                }
            }
            function p() {
                c.addClass(this.underlay, "yui-tt-shadow-visible");
                if (h.ie) {
                    this.forceUnderlayRedraw();
                }
            }
            function o() {
                c.removeClass(this.underlay, "yui-tt-shadow-visible");
            }
            function u() {
                var x = this.underlay,
                    w, v, z, y;
                if (!x) {
                    w = this.element;
                    v = YAHOO.widget.Module;
                    z = h.ie;
                    y = this;
                    if (!f) {
                        f = document.createElement("div");
                        f.className = "yui-tt-shadow";
                    }
                    x = f.cloneNode(false);
                    w.appendChild(x);
                    this.underlay = x;
                    this._shadow = this.underlay;
                    p.call(this);
                    this.subscribe("beforeShow", p);
                    this.subscribe("hide", o);
                    if (g) {
                        window.setTimeout(function () {
                            t.call(y);
                        }, 0);
                        this.cfg.subscribeToConfigEvent("width", t);
                        this.cfg.subscribeToConfigEvent("height", t);
                        this.subscribe("changeContent", t);
                        v.textResizeEvent.subscribe(t, this, true);
                        this.subscribe("destroy", function () {
                            v.textResizeEvent.unsubscribe(t, this);
                        });
                    }
                }
            }
            function q() {
                u.call(this);
                this.unsubscribe("beforeShow", q);
            }
            if (this.cfg.getProperty("visible")) {
                u.call(this);
            } else {
                this.subscribe("beforeShow", q);
            }
        },
        forceUnderlayRedraw: function () {
            var o = this;
            c.addClass(o.underlay, "yui-force-redraw");
            setTimeout(function () {
                c.removeClass(o.underlay, "yui-force-redraw");
            }, 0);
        },
        destroy: function () {
            this._removeEventListeners();
            j.superclass.destroy.call(this);
        },
        toString: function () {
            return "Tooltip " + this.id;
        }
    });
}());
(function () {
    YAHOO.widget.Panel = function (v, u) {
        YAHOO.widget.Panel.superclass.constructor.call(this, v, u);
    };
    var s = null;
    var e = YAHOO.lang,
        f = YAHOO.util,
        a = f.Dom,
        t = f.Event,
        m = f.CustomEvent,
        k = YAHOO.util.KeyListener,
        i = f.Config,
        h = YAHOO.widget.Overlay,
        o = YAHOO.widget.Panel,
        l = YAHOO.env.ua,
        p = (l.ie && (l.ie <= 6 || document.compatMode == "BackCompat")),
        g, q, c, d = {
            "BEFORE_SHOW_MASK": "beforeShowMask",
            "BEFORE_HIDE_MASK": "beforeHideMask",
            "SHOW_MASK": "showMask",
            "HIDE_MASK": "hideMask",
            "DRAG": "drag"
        },
        n = {
            "CLOSE": {
                key: "close",
                value: true,
                validator: e.isBoolean,
                supercedes: ["visible"]
            },
            "DRAGGABLE": {
                key: "draggable",
                value: (f.DD ? true : false),
                validator: e.isBoolean,
                supercedes: ["visible"]
            },
            "DRAG_ONLY": {
                key: "dragonly",
                value: false,
                validator: e.isBoolean,
                supercedes: ["draggable"]
            },
            "UNDERLAY": {
                key: "underlay",
                value: "shadow",
                supercedes: ["visible"]
            },
            "MODAL": {
                key: "modal",
                value: false,
                validator: e.isBoolean,
                supercedes: ["visible", "zindex"]
            },
            "KEY_LISTENERS": {
                key: "keylisteners",
                suppressEvent: true,
                supercedes: ["visible"]
            },
            "STRINGS": {
                key: "strings",
                supercedes: ["close"],
                validator: e.isObject,
                value: {
                    close: "Close"
                }
            }
        };
    o.CSS_PANEL = "yui-panel";
    o.CSS_PANEL_CONTAINER = "yui-panel-container";
    o.FOCUSABLE = ["a", "button", "select", "textarea", "input", "iframe"];

    function j(v, u) {
        if (!this.header && this.cfg.getProperty("draggable")) {
            this.setHeader("&#160;");
        }
    }
    function r(v, u, w) {
        var z = w[0],
            x = w[1],
            y = this.cfg,
            A = y.getProperty("width");
        if (A == x) {
            y.setProperty("width", z);
        }
        this.unsubscribe("hide", r, w);
    }
    function b(v, u) {
        var y, x, w;
        if (p) {
            y = this.cfg;
            x = y.getProperty("width");
            if (!x || x == "auto") {
                w = (this.element.offsetWidth + "px");
                y.setProperty("width", w);
                this.subscribe("hide", r, [(x || ""), w]);
            }
        }
    }
    YAHOO.extend(o, h, {
        init: function (v, u) {
            o.superclass.init.call(this, v);
            this.beforeInitEvent.fire(o);
            a.addClass(this.element, o.CSS_PANEL);
            this.buildWrapper();
            if (u) {
                this.cfg.applyConfig(u, true);
            }
            this.subscribe("showMask", this._addFocusHandlers);
            this.subscribe("hideMask", this._removeFocusHandlers);
            this.subscribe("beforeRender", j);
            this.subscribe("render", function () {
                this.setFirstLastFocusable();
                this.subscribe("changeContent", this.setFirstLastFocusable);
            });
            this.subscribe("show", this._focusOnShow);
            this.initEvent.fire(o);
        },
        _onElementFocus: function (z) {
            if (s === this) {
                var y = t.getTarget(z),
                    x = document.documentElement,
                    v = (y !== x && y !== window);
                if (v && y !== this.element && y !== this.mask && !a.isAncestor(this.element, y)) {
                    try {
                        this._focusFirstModal();
                    } catch (w) {
                        try {
                            if (v && y !== document.body) {
                                y.blur();
                            }
                        } catch (u) {}
                    }
                }
            }
        },
        _focusFirstModal: function () {
            var u = this.firstElement;
            if (u) {
                u.focus();
            } else {
                if (this._modalFocus) {
                    this._modalFocus.focus();
                } else {
                    this.innerElement.focus();
                }
            }
        },
        _addFocusHandlers: function (v, u) {
            if (!this.firstElement) {
                if (l.webkit || l.opera) {
                    if (!this._modalFocus) {
                        this._createHiddenFocusElement();
                    }
                } else {
                    this.innerElement.tabIndex = 0;
                }
            }
            this._setTabLoop(this.firstElement, this.lastElement);
            t.onFocus(document.documentElement, this._onElementFocus, this, true);
            s = this;
        },
        _createHiddenFocusElement: function () {
            var u = document.createElement("button");
            u.style.height = "1px";
            u.style.width = "1px";
            u.style.position = "absolute";
            u.style.left = "-10000em";
            u.style.opacity = 0;
            u.tabIndex = -1;
            this.innerElement.appendChild(u);
            this._modalFocus = u;
        },
        _removeFocusHandlers: function (v, u) {
            t.removeFocusListener(document.documentElement, this._onElementFocus, this);
            if (s == this) {
                s = null;
            }
        },
        _focusOnShow: function (v, u, w) {
            if (u && u[1]) {
                t.stopEvent(u[1]);
            }
            if (!this.focusFirst(v, u, w)) {
                if (this.cfg.getProperty("modal")) {
                    this._focusFirstModal();
                }
            }
        },
        focusFirst: function (w, u, z) {
            var v = this.firstElement,
                y = false;
            if (u && u[1]) {
                t.stopEvent(u[1]);
            }
            if (v) {
                try {
                    v.focus();
                    y = true;
                } catch (x) {}
            }
            return y;
        },
        focusLast: function (w, u, z) {
            var v = this.lastElement,
                y = false;
            if (u && u[1]) {
                t.stopEvent(u[1]);
            }
            if (v) {
                try {
                    v.focus();
                    y = true;
                } catch (x) {}
            }
            return y;
        },
        _setTabLoop: function (u, v) {
            this.setTabLoop(u, v);
        },
        setTabLoop: function (x, z) {
            var v = this.preventBackTab,
                w = this.preventTabOut,
                u = this.showEvent,
                y = this.hideEvent;
            if (v) {
                v.disable();
                u.unsubscribe(v.enable, v);
                y.unsubscribe(v.disable, v);
                v = this.preventBackTab = null;
            }
            if (w) {
                w.disable();
                u.unsubscribe(w.enable, w);
                y.unsubscribe(w.disable, w);
                w = this.preventTabOut = null;
            }
            if (x) {
                this.preventBackTab = new k(x, {
                    shift: true,
                    keys: 9
                }, {
                    fn: this.focusLast,
                    scope: this,
                    correctScope: true
                });
                v = this.preventBackTab;
                u.subscribe(v.enable, v, true);
                y.subscribe(v.disable, v, true);
            }
            if (z) {
                this.preventTabOut = new k(z, {
                    shift: false,
                    keys: 9
                }, {
                    fn: this.focusFirst,
                    scope: this,
                    correctScope: true
                });
                w = this.preventTabOut;
                u.subscribe(w.enable, w, true);
                y.subscribe(w.disable, w, true);
            }
        },
        getFocusableElements: function (v) {
            v = v || this.innerElement;
            var x = {},
                u = this;
            for (var w = 0; w < o.FOCUSABLE.length; w++) {
                x[o.FOCUSABLE[w]] = true;
            }
            return a.getElementsBy(function (y) {
                return u._testIfFocusable(y, x);
            }, null, v);
        },
        _testIfFocusable: function (u, v) {
            if (u.focus && u.type !== "hidden" && !u.disabled && v[u.tagName.toLowerCase()]) {
                return true;
            }
            return false;
        },
        setFirstLastFocusable: function () {
            this.firstElement = null;
            this.lastElement = null;
            var u = this.getFocusableElements();
            this.focusableElements = u;
            if (u.length > 0) {
                this.firstElement = u[0];
                this.lastElement = u[u.length - 1];
            }
            if (this.cfg.getProperty("modal")) {
                this._setTabLoop(this.firstElement, this.lastElement);
            }
        },
        initEvents: function () {
            o.superclass.initEvents.call(this);
            var u = m.LIST;
            this.showMaskEvent = this.createEvent(d.SHOW_MASK);
            this.showMaskEvent.signature = u;
            this.beforeShowMaskEvent = this.createEvent(d.BEFORE_SHOW_MASK);
            this.beforeShowMaskEvent.signature = u;
            this.hideMaskEvent = this.createEvent(d.HIDE_MASK);
            this.hideMaskEvent.signature = u;
            this.beforeHideMaskEvent = this.createEvent(d.BEFORE_HIDE_MASK);
            this.beforeHideMaskEvent.signature = u;
            this.dragEvent = this.createEvent(d.DRAG);
            this.dragEvent.signature = u;
        },
        initDefaultConfig: function () {
            o.superclass.initDefaultConfig.call(this);
            this.cfg.addProperty(n.CLOSE.key, {
                handler: this.configClose,
                value: n.CLOSE.value,
                validator: n.CLOSE.validator,
                supercedes: n.CLOSE.supercedes
            });
            this.cfg.addProperty(n.DRAGGABLE.key, {
                handler: this.configDraggable,
                value: (f.DD) ? true : false,
                validator: n.DRAGGABLE.validator,
                supercedes: n.DRAGGABLE.supercedes
            });
            this.cfg.addProperty(n.DRAG_ONLY.key, {
                value: n.DRAG_ONLY.value,
                validator: n.DRAG_ONLY.validator,
                supercedes: n.DRAG_ONLY.supercedes
            });
            this.cfg.addProperty(n.UNDERLAY.key, {
                handler: this.configUnderlay,
                value: n.UNDERLAY.value,
                supercedes: n.UNDERLAY.supercedes
            });
            this.cfg.addProperty(n.MODAL.key, {
                handler: this.configModal,
                value: n.MODAL.value,
                validator: n.MODAL.validator,
                supercedes: n.MODAL.supercedes
            });
            this.cfg.addProperty(n.KEY_LISTENERS.key, {
                handler: this.configKeyListeners,
                suppressEvent: n.KEY_LISTENERS.suppressEvent,
                supercedes: n.KEY_LISTENERS.supercedes
            });
            this.cfg.addProperty(n.STRINGS.key, {
                value: n.STRINGS.value,
                handler: this.configStrings,
                validator: n.STRINGS.validator,
                supercedes: n.STRINGS.supercedes
            });
        },
        configClose: function (y, v, z) {
            var A = v[0],
                x = this.close,
                u = this.cfg.getProperty("strings"),
                w;
            if (A) {
                if (!x) {
                    if (!c) {
                        c = document.createElement("a");
                        c.className = "container-close";
                        c.href = "#";
                    }
                    x = c.cloneNode(true);
                    w = this.innerElement.firstChild;
                    if (w) {
                        this.innerElement.insertBefore(x, w);
                    } else {
                        this.innerElement.appendChild(x);
                    }
                    x.innerHTML = (u && u.close) ? u.close : "&#160;";
                    t.on(x, "click", this._doClose, this, true);
                    this.close = x;
                } else {
                    x.style.display = "block";
                }
            } else {
                if (x) {
                    x.style.display = "none";
                }
            }
        },
        _doClose: function (u) {
            t.preventDefault(u);
            this.hide();
        },
        configDraggable: function (v, u, w) {
            var x = u[0];
            if (x) {
                if (!f.DD) {
                    this.cfg.setProperty("draggable", false);
                    return;
                }
                if (this.header) {
                    a.setStyle(this.header, "cursor", "move");
                    this.registerDragDrop();
                }
                this.subscribe("beforeShow", b);
            } else {
                if (this.dd) {
                    this.dd.unreg();
                }
                if (this.header) {
                    a.setStyle(this.header, "cursor", "auto");
                }
                this.unsubscribe("beforeShow", b);
            }
        },
        configUnderlay: function (D, C, z) {
            var B = (this.platform == "mac" && l.gecko),
                E = C[0].toLowerCase(),
                v = this.underlay,
                w = this.element;

            function x() {
                var F = false;
                if (!v) {
                    if (!q) {
                        q = document.createElement("div");
                        q.className = "underlay";
                    }
                    v = q.cloneNode(false);
                    this.element.appendChild(v);
                    this.underlay = v;
                    if (p) {
                        this.sizeUnderlay();
                        this.cfg.subscribeToConfigEvent("width", this.sizeUnderlay);
                        this.cfg.subscribeToConfigEvent("height", this.sizeUnderlay);
                        this.changeContentEvent.subscribe(this.sizeUnderlay);
                        YAHOO.widget.Module.textResizeEvent.subscribe(this.sizeUnderlay, this, true);
                    }
                    if (l.webkit && l.webkit < 420) {
                        this.changeContentEvent.subscribe(this.forceUnderlayRedraw);
                    }
                    F = true;
                }
            }
            function A() {
                var F = x.call(this);
                if (!F && p) {
                    this.sizeUnderlay();
                }
                this._underlayDeferred = false;
                this.beforeShowEvent.unsubscribe(A);
            }
            function y() {
                if (this._underlayDeferred) {
                    this.beforeShowEvent.unsubscribe(A);
                    this._underlayDeferred = false;
                }
                if (v) {
                    this.cfg.unsubscribeFromConfigEvent("width", this.sizeUnderlay);
                    this.cfg.unsubscribeFromConfigEvent("height", this.sizeUnderlay);
                    this.changeContentEvent.unsubscribe(this.sizeUnderlay);
                    this.changeContentEvent.unsubscribe(this.forceUnderlayRedraw);
                    YAHOO.widget.Module.textResizeEvent.unsubscribe(this.sizeUnderlay, this, true);
                    this.element.removeChild(v);
                    this.underlay = null;
                }
            }
            switch (E) {
            case "shadow":
                a.removeClass(w, "matte");
                a.addClass(w, "shadow");
                break;
            case "matte":
                if (!B) {
                    y.call(this);
                }
                a.removeClass(w, "shadow");
                a.addClass(w, "matte");
                break;
            default:
                if (!B) {
                    y.call(this);
                }
                a.removeClass(w, "shadow");
                a.removeClass(w, "matte");
                break;
            }
            if ((E == "shadow") || (B && !v)) {
                if (this.cfg.getProperty("visible")) {
                    var u = x.call(this);
                    if (!u && p) {
                        this.sizeUnderlay();
                    }
                } else {
                    if (!this._underlayDeferred) {
                        this.beforeShowEvent.subscribe(A);
                        this._underlayDeferred = true;
                    }
                }
            }
        },
        configModal: function (v, u, x) {
            var w = u[0];
            if (w) {
                if (!this._hasModalityEventListeners) {
                    this.subscribe("beforeShow", this.buildMask);
                    this.subscribe("beforeShow", this.bringToTop);
                    this.subscribe("beforeShow", this.showMask);
                    this.subscribe("hide", this.hideMask);
                    h.windowResizeEvent.subscribe(this.sizeMask, this, true);
                    this._hasModalityEventListeners = true;
                }
            } else {
                if (this._hasModalityEventListeners) {
                    if (this.cfg.getProperty("visible")) {
                        this.hideMask();
                        this.removeMask();
                    }
                    this.unsubscribe("beforeShow", this.buildMask);
                    this.unsubscribe("beforeShow", this.bringToTop);
                    this.unsubscribe("beforeShow", this.showMask);
                    this.unsubscribe("hide", this.hideMask);
                    h.windowResizeEvent.unsubscribe(this.sizeMask, this);
                    this._hasModalityEventListeners = false;
                }
            }
        },
        removeMask: function () {
            var v = this.mask,
                u;
            if (v) {
                this.hideMask();
                u = v.parentNode;
                if (u) {
                    u.removeChild(v);
                }
                this.mask = null;
            }
        },
        configKeyListeners: function (x, u, A) {
            var w = u[0],
                z, y, v;
            if (w) {
                if (w instanceof Array) {
                    y = w.length;
                    for (v = 0; v < y; v++) {
                        z = w[v];
                        if (!i.alreadySubscribed(this.showEvent, z.enable, z)) {
                            this.showEvent.subscribe(z.enable, z, true);
                        }
                        if (!i.alreadySubscribed(this.hideEvent, z.disable, z)) {
                            this.hideEvent.subscribe(z.disable, z, true);
                            this.destroyEvent.subscribe(z.disable, z, true);
                        }
                    }
                } else {
                    if (!i.alreadySubscribed(this.showEvent, w.enable, w)) {
                        this.showEvent.subscribe(w.enable, w, true);
                    }
                    if (!i.alreadySubscribed(this.hideEvent, w.disable, w)) {
                        this.hideEvent.subscribe(w.disable, w, true);
                        this.destroyEvent.subscribe(w.disable, w, true);
                    }
                }
            }
        },
        configStrings: function (v, u, w) {
            var x = e.merge(n.STRINGS.value, u[0]);
            this.cfg.setProperty(n.STRINGS.key, x, true);
        },
        configHeight: function (x, v, y) {
            var u = v[0],
                w = this.innerElement;
            a.setStyle(w, "height", u);
            this.cfg.refireEvent("iframe");
        },
        _autoFillOnHeightChange: function (x, v, w) {
            o.superclass._autoFillOnHeightChange.apply(this, arguments);
            if (p) {
                var u = this;
                setTimeout(function () {
                    u.sizeUnderlay();
                }, 0);
            }
        },
        configWidth: function (x, u, y) {
            var w = u[0],
                v = this.innerElement;
            a.setStyle(v, "width", w);
            this.cfg.refireEvent("iframe");
        },
        configzIndex: function (v, u, x) {
            o.superclass.configzIndex.call(this, v, u, x);
            if (this.mask || this.cfg.getProperty("modal") === true) {
                var w = a.getStyle(this.element, "zIndex");
                if (!w || isNaN(w)) {
                    w = 0;
                }
                if (w === 0) {
                    this.cfg.setProperty("zIndex", 1);
                } else {
                    this.stackMask();
                }
            }
        },
        buildWrapper: function () {
            var w = this.element.parentNode,
                u = this.element,
                v = document.createElement("div");
            v.className = o.CSS_PANEL_CONTAINER;
            v.id = u.id + "_c";
            if (w) {
                w.insertBefore(v, u);
            }
            v.appendChild(u);
            this.element = v;
            this.innerElement = u;
            a.setStyle(this.innerElement, "visibility", "inherit");
        },
        sizeUnderlay: function () {
            var v = this.underlay,
                u;
            if (v) {
                u = this.element;
                v.style.width = u.offsetWidth + "px";
                v.style.height = u.offsetHeight + "px";
            }
        },
        registerDragDrop: function () {
            var v = this;
            if (this.header) {
                if (!f.DD) {
                    return;
                }
                var u = (this.cfg.getProperty("dragonly") === true);
                this.dd = new f.DD(this.element.id, this.id, {
                    dragOnly: u
                });
                if (!this.header.id) {
                    this.header.id = this.id + "_h";
                }
                this.dd.startDrag = function () {
                    var x, z, w, C, B, A;
                    if (YAHOO.env.ua.ie == 6) {
                        a.addClass(v.element, "drag");
                    }
                    if (v.cfg.getProperty("constraintoviewport")) {
                        var y = h.VIEWPORT_OFFSET;
                        x = v.element.offsetHeight;
                        z = v.element.offsetWidth;
                        w = a.getViewportWidth();
                        C = a.getViewportHeight();
                        B = a.getDocumentScrollLeft();
                        A = a.getDocumentScrollTop();
                        if (x + y < C) {
                            this.minY = A + y;
                            this.maxY = A + C - x - y;
                        } else {
                            this.minY = A + y;
                            this.maxY = A + y;
                        }
                        if (z + y < w) {
                            this.minX = B + y;
                            this.maxX = B + w - z - y;
                        } else {
                            this.minX = B + y;
                            this.maxX = B + y;
                        }
                        this.constrainX = true;
                        this.constrainY = true;
                    } else {
                        this.constrainX = false;
                        this.constrainY = false;
                    }
                    v.dragEvent.fire("startDrag", arguments);
                };
                this.dd.onDrag = function () {
                    v.syncPosition();
                    v.cfg.refireEvent("iframe");
                    if (this.platform == "mac" && YAHOO.env.ua.gecko) {
                        this.showMacGeckoScrollbars();
                    }
                    v.dragEvent.fire("onDrag", arguments);
                };
                this.dd.endDrag = function () {
                    if (YAHOO.env.ua.ie == 6) {
                        a.removeClass(v.element, "drag");
                    }
                    v.dragEvent.fire("endDrag", arguments);
                    v.moveEvent.fire(v.cfg.getProperty("xy"));
                };
                this.dd.setHandleElId(this.header.id);
                this.dd.addInvalidHandleType("INPUT");
                this.dd.addInvalidHandleType("SELECT");
                this.dd.addInvalidHandleType("TEXTAREA");
            }
        },
        buildMask: function () {
            var u = this.mask;
            if (!u) {
                if (!g) {
                    g = document.createElement("div");
                    g.className = "mask";
                    g.innerHTML = "&#160;";
                }
                u = g.cloneNode(true);
                u.id = this.id + "_mask";
                document.body.insertBefore(u, document.body.firstChild);
                this.mask = u;
                if (YAHOO.env.ua.gecko && this.platform == "mac") {
                    a.addClass(this.mask, "block-scrollbars");
                }
                this.stackMask();
            }
        },
        hideMask: function () {
            if (this.cfg.getProperty("modal") && this.mask && this.beforeHideMaskEvent.fire()) {
                this.mask.style.display = "none";
                a.removeClass(document.body, "masked");
                this.hideMaskEvent.fire();
            }
        },
        showMask: function () {
            if (this.cfg.getProperty("modal") && this.mask && this.beforeShowMaskEvent.fire()) {
                a.addClass(document.body, "masked");
                this.sizeMask();
                this.mask.style.display = "block";
                this.showMaskEvent.fire();
            }
        },
        sizeMask: function () {
            if (this.mask) {
                var v = this.mask,
                    w = a.getViewportWidth(),
                    u = a.getViewportHeight();
                if (v.offsetHeight > u) {
                    v.style.height = u + "px";
                }
                if (v.offsetWidth > w) {
                    v.style.width = w + "px";
                }
                v.style.height = a.getDocumentHeight() + "px";
                v.style.width = a.getDocumentWidth() + "px";
            }
        },
        stackMask: function () {
            if (this.mask) {
                var u = a.getStyle(this.element, "zIndex");
                if (!YAHOO.lang.isUndefined(u) && !isNaN(u)) {
                    a.setStyle(this.mask, "zIndex", u - 1);
                }
            }
        },
        render: function (u) {
            return o.superclass.render.call(this, u, this.innerElement);
        },
        _renderHeader: function (u) {
            u = u || this.innerElement;
            o.superclass._renderHeader.call(this, u);
        },
        _renderBody: function (u) {
            u = u || this.innerElement;
            o.superclass._renderBody.call(this, u);
        },
        _renderFooter: function (u) {
            u = u || this.innerElement;
            o.superclass._renderFooter.call(this, u);
        },
        destroy: function (u) {
            h.windowResizeEvent.unsubscribe(this.sizeMask, this);
            this.removeMask();
            if (this.close) {
                t.purgeElement(this.close);
            }
            o.superclass.destroy.call(this, u);
        },
        forceUnderlayRedraw: function () {
            var v = this.underlay;
            a.addClass(v, "yui-force-redraw");
            setTimeout(function () {
                a.removeClass(v, "yui-force-redraw");
            }, 0);
        },
        toString: function () {
            return "Panel " + this.id;
        }
    });
}());
(function () {
    YAHOO.widget.Dialog = function (j, i) {
        YAHOO.widget.Dialog.superclass.constructor.call(this, j, i);
    };
    var b = YAHOO.util.Event,
        g = YAHOO.util.CustomEvent,
        e = YAHOO.util.Dom,
        a = YAHOO.widget.Dialog,
        f = YAHOO.lang,
        h = {
            "BEFORE_SUBMIT": "beforeSubmit",
            "SUBMIT": "submit",
            "MANUAL_SUBMIT": "manualSubmit",
            "ASYNC_SUBMIT": "asyncSubmit",
            "FORM_SUBMIT": "formSubmit",
            "CANCEL": "cancel"
        },
        c = {
            "POST_METHOD": {
                key: "postmethod",
                value: "async"
            },
            "POST_DATA": {
                key: "postdata",
                value: null
            },
            "BUTTONS": {
                key: "buttons",
                value: "none",
                supercedes: ["visible"]
            },
            "HIDEAFTERSUBMIT": {
                key: "hideaftersubmit",
                value: true
            }
        };
    a.CSS_DIALOG = "yui-dialog";

    function d() {
        var m = this._aButtons,
            k, l, j;
        if (f.isArray(m)) {
            k = m.length;
            if (k > 0) {
                j = k - 1;
                do {
                    l = m[j];
                    if (YAHOO.widget.Button && l instanceof YAHOO.widget.Button) {
                        l.destroy();
                    } else {
                        if (l.tagName.toUpperCase() == "BUTTON") {
                            b.purgeElement(l);
                            b.purgeElement(l, false);
                        }
                    }
                } while (j--);
            }
        }
    }
    YAHOO.extend(a, YAHOO.widget.Panel, {
        form: null,
        initDefaultConfig: function () {
            a.superclass.initDefaultConfig.call(this);
            this.callback = {
                success: null,
                failure: null,
                argument: null
            };
            this.cfg.addProperty(c.POST_METHOD.key, {
                handler: this.configPostMethod,
                value: c.POST_METHOD.value,
                validator: function (i) {
                    if (i != "form" && i != "async" && i != "none" && i != "manual") {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
            this.cfg.addProperty(c.POST_DATA.key, {
                value: c.POST_DATA.value
            });
            this.cfg.addProperty(c.HIDEAFTERSUBMIT.key, {
                value: c.HIDEAFTERSUBMIT.value
            });
            this.cfg.addProperty(c.BUTTONS.key, {
                handler: this.configButtons,
                value: c.BUTTONS.value,
                supercedes: c.BUTTONS.supercedes
            });
        },
        initEvents: function () {
            a.superclass.initEvents.call(this);
            var i = g.LIST;
            this.beforeSubmitEvent = this.createEvent(h.BEFORE_SUBMIT);
            this.beforeSubmitEvent.signature = i;
            this.submitEvent = this.createEvent(h.SUBMIT);
            this.submitEvent.signature = i;
            this.manualSubmitEvent = this.createEvent(h.MANUAL_SUBMIT);
            this.manualSubmitEvent.signature = i;
            this.asyncSubmitEvent = this.createEvent(h.ASYNC_SUBMIT);
            this.asyncSubmitEvent.signature = i;
            this.formSubmitEvent = this.createEvent(h.FORM_SUBMIT);
            this.formSubmitEvent.signature = i;
            this.cancelEvent = this.createEvent(h.CANCEL);
            this.cancelEvent.signature = i;
        },
        init: function (j, i) {
            a.superclass.init.call(this, j);
            this.beforeInitEvent.fire(a);
            e.addClass(this.element, a.CSS_DIALOG);
            this.cfg.setProperty("visible", false);
            if (i) {
                this.cfg.applyConfig(i, true);
            }
            this.beforeHideEvent.subscribe(this.blurButtons, this, true);
            this.subscribe("changeBody", this.registerForm);
            this.initEvent.fire(a);
        },
        doSubmit: function () {
            var q = YAHOO.util.Connect,
                r = this.form,
                l = false,
                o = false,
                s, n, m, j;
            switch (this.cfg.getProperty("postmethod")) {
            case "async":
                s = r.elements;
                n = s.length;
                if (n > 0) {
                    m = n - 1;
                    do {
                        if (s[m].type == "file") {
                            l = true;
                            break;
                        }
                    } while (m--);
                }
                if (l && YAHOO.env.ua.ie && this.isSecure) {
                    o = true;
                }
                j = this._getFormAttributes(r);
                q.setForm(r, l, o);
                var k = this.cfg.getProperty("postdata");
                var p = q.asyncRequest(j.method, j.action, this.callback, k);
                this.asyncSubmitEvent.fire(p);
                break;
            case "form":
                r.submit();
                this.formSubmitEvent.fire();
                break;
            case "none":
            case "manual":
                this.manualSubmitEvent.fire();
                break;
            }
        },
        _getFormAttributes: function (k) {
            var i = {
                method: null,
                action: null
            };
            if (k) {
                if (k.getAttributeNode) {
                    var j = k.getAttributeNode("action");
                    var l = k.getAttributeNode("method");
                    if (j) {
                        i.action = j.value;
                    }
                    if (l) {
                        i.method = l.value;
                    }
                } else {
                    i.action = k.getAttribute("action");
                    i.method = k.getAttribute("method");
                }
            }
            i.method = (f.isString(i.method) ? i.method : "POST").toUpperCase();
            i.action = f.isString(i.action) ? i.action : "";
            return i;
        },
        registerForm: function () {
            var i = this.element.getElementsByTagName("form")[0];
            if (this.form) {
                if (this.form == i && e.isAncestor(this.element, this.form)) {
                    return;
                } else {
                    b.purgeElement(this.form);
                    this.form = null;
                }
            }
            if (!i) {
                i = document.createElement("form");
                i.name = "frm_" + this.id;
                this.body.appendChild(i);
            }
            if (i) {
                this.form = i;
                b.on(i, "submit", this._submitHandler, this, true);
            }
        },
        _submitHandler: function (i) {
            b.stopEvent(i);
            this.submit();
            this.form.blur();
        },
        setTabLoop: function (i, j) {
            i = i || this.firstButton;
            j = j || this.lastButton;
            a.superclass.setTabLoop.call(this, i, j);
        },
        _setTabLoop: function (i, j) {
            i = i || this.firstButton;
            j = this.lastButton || j;
            this.setTabLoop(i, j);
        },
        setFirstLastFocusable: function () {
            a.superclass.setFirstLastFocusable.call(this);
            var k, j, m, n = this.focusableElements;
            this.firstFormElement = null;
            this.lastFormElement = null;
            if (this.form && n && n.length > 0) {
                j = n.length;
                for (k = 0; k < j; ++k) {
                    m = n[k];
                    if (this.form === m.form) {
                        this.firstFormElement = m;
                        break;
                    }
                }
                for (k = j - 1; k >= 0; --k) {
                    m = n[k];
                    if (this.form === m.form) {
                        this.lastFormElement = m;
                        break;
                    }
                }
            }
        },
        configClose: function (j, i, k) {
            a.superclass.configClose.apply(this, arguments);
        },
        _doClose: function (i) {
            b.preventDefault(i);
            this.cancel();
        },
        configButtons: function (t, s, n) {
            var o = YAHOO.widget.Button,
                v = s[0],
                l = this.innerElement,
                u, q, k, r, p, j, m;
            d.call(this);
            this._aButtons = null;
            if (f.isArray(v)) {
                p = document.createElement("span");
                p.className = "button-group";
                r = v.length;
                this._aButtons = [];
                this.defaultHtmlButton = null;
                for (m = 0; m < r; m++) {
                    u = v[m];
                    if (o) {
                        k = new o({
                            label: u.text,
                            type: u.type
                        });
                        k.appendTo(p);
                        q = k.get("element");
                        if (u.isDefault) {
                            k.addClass("default");
                            this.defaultHtmlButton = q;
                        }
                        if (f.isFunction(u.handler)) {
                            k.set("onclick", {
                                fn: u.handler,
                                obj: this,
                                scope: this
                            });
                        } else {
                            if (f.isObject(u.handler) && f.isFunction(u.handler.fn)) {
                                k.set("onclick", {
                                    fn: u.handler.fn,
                                    obj: ((!f.isUndefined(u.handler.obj)) ? u.handler.obj : this),
                                    scope: (u.handler.scope || this)
                                });
                            }
                        }
                        this._aButtons[this._aButtons.length] = k;
                    } else {
                        q = document.createElement("button");
                        q.setAttribute("type", "button");
                        if (u.isDefault) {
                            q.className = "default";
                            this.defaultHtmlButton = q;
                        }
                        q.innerHTML = u.text;
                        if (f.isFunction(u.handler)) {
                            b.on(q, "click", u.handler, this, true);
                        } else {
                            if (f.isObject(u.handler) && f.isFunction(u.handler.fn)) {
                                b.on(q, "click", u.handler.fn, ((!f.isUndefined(u.handler.obj)) ? u.handler.obj : this), (u.handler.scope || this));
                            }
                        }
                        p.appendChild(q);
                        this._aButtons[this._aButtons.length] = q;
                    }
                    u.htmlButton = q;
                    if (m === 0) {
                        this.firstButton = q;
                    }
                    if (m == (r - 1)) {
                        this.lastButton = q;
                    }
                }
                this.setFooter(p);
                j = this.footer;
                if (e.inDocument(this.element) && !e.isAncestor(l, j)) {
                    l.appendChild(j);
                }
                this.buttonSpan = p;
            } else {
                p = this.buttonSpan;
                j = this.footer;
                if (p && j) {
                    j.removeChild(p);
                    this.buttonSpan = null;
                    this.firstButton = null;
                    this.lastButton = null;
                    this.defaultHtmlButton = null;
                }
            }
            this.changeContentEvent.fire();
        },
        getButtons: function () {
            return this._aButtons || null;
        },
        focusFirst: function (k, i, n) {
            var j = this.firstFormElement,
                m = false;
            if (i && i[1]) {
                b.stopEvent(i[1]);
                if (i[0] === 9 && this.firstElement) {
                    j = this.firstElement;
                }
            }
            if (j) {
                try {
                    j.focus();
                    m = true;
                } catch (l) {}
            } else {
                if (this.defaultHtmlButton) {
                    m = this.focusDefaultButton();
                } else {
                    m = this.focusFirstButton();
                }
            }
            return m;
        },
        focusLast: function (k, i, n) {
            var o = this.cfg.getProperty("buttons"),
                j = this.lastFormElement,
                m = false;
            if (i && i[1]) {
                b.stopEvent(i[1]);
                if (i[0] === 9 && this.lastElement) {
                    j = this.lastElement;
                }
            }
            if (o && f.isArray(o)) {
                m = this.focusLastButton();
            } else {
                if (j) {
                    try {
                        j.focus();
                        m = true;
                    } catch (l) {}
                }
            }
            return m;
        },
        _getButton: function (j) {
            var i = YAHOO.widget.Button;
            if (i && j && j.nodeName && j.id) {
                j = i.getButton(j.id) || j;
            }
            return j;
        },
        focusDefaultButton: function () {
            var i = this._getButton(this.defaultHtmlButton),
                k = false;
            if (i) {
                try {
                    i.focus();
                    k = true;
                } catch (j) {}
            }
            return k;
        },
        blurButtons: function () {
            var o = this.cfg.getProperty("buttons"),
                l, n, k, j;
            if (o && f.isArray(o)) {
                l = o.length;
                if (l > 0) {
                    j = (l - 1);
                    do {
                        n = o[j];
                        if (n) {
                            k = this._getButton(n.htmlButton);
                            if (k) {
                                try {
                                    k.blur();
                                } catch (m) {}
                            }
                        }
                    } while (j--);
                }
            }
        },
        focusFirstButton: function () {
            var m = this.cfg.getProperty("buttons"),
                k, i, l = false;
            if (m && f.isArray(m)) {
                k = m[0];
                if (k) {
                    i = this._getButton(k.htmlButton);
                    if (i) {
                        try {
                            i.focus();
                            l = true;
                        } catch (j) {}
                    }
                }
            }
            return l;
        },
        focusLastButton: function () {
            var n = this.cfg.getProperty("buttons"),
                j, l, i, m = false;
            if (n && f.isArray(n)) {
                j = n.length;
                if (j > 0) {
                    l = n[(j - 1)];
                    if (l) {
                        i = this._getButton(l.htmlButton);
                        if (i) {
                            try {
                                i.focus();
                                m = true;
                            } catch (k) {}
                        }
                    }
                }
            }
            return m;
        },
        configPostMethod: function (j, i, k) {
            this.registerForm();
        },
        validate: function () {
            return true;
        },
        submit: function () {
            if (this.validate()) {
                if (this.beforeSubmitEvent.fire()) {
                    this.doSubmit();
                    this.submitEvent.fire();
                    if (this.cfg.getProperty("hideaftersubmit")) {
                        this.hide();
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        },
        cancel: function () {
            this.cancelEvent.fire();
            this.hide();
        },
        getData: function () {
            var A = this.form,
                k, t, w, m, u, r, q, j, x, l, y, B, p, C, o, z, v;

            function s(n) {
                var i = n.tagName.toUpperCase();
                return ((i == "INPUT" || i == "TEXTAREA" || i == "SELECT") && n.name == m);
            }
            if (A) {
                k = A.elements;
                t = k.length;
                w = {};
                for (z = 0; z < t; z++) {
                    m = k[z].name;
                    u = e.getElementsBy(s, "*", A);
                    r = u.length;
                    if (r > 0) {
                        if (r == 1) {
                            u = u[0];
                            q = u.type;
                            j = u.tagName.toUpperCase();
                            switch (j) {
                            case "INPUT":
                                if (q == "checkbox") {
                                    w[m] = u.checked;
                                } else {
                                    if (q != "radio") {
                                        w[m] = u.value;
                                    }
                                }
                                break;
                            case "TEXTAREA":
                                w[m] = u.value;
                                break;
                            case "SELECT":
                                x = u.options;
                                l = x.length;
                                y = [];
                                for (v = 0; v < l; v++) {
                                    B = x[v];
                                    if (B.selected) {
                                        o = B.attributes.value;
                                        y[y.length] = (o && o.specified) ? B.value : B.text;
                                    }
                                }
                                w[m] = y;
                                break;
                            }
                        } else {
                            q = u[0].type;
                            switch (q) {
                            case "radio":
                                for (v = 0; v < r; v++) {
                                    p = u[v];
                                    if (p.checked) {
                                        w[m] = p.value;
                                        break;
                                    }
                                }
                                break;
                            case "checkbox":
                                y = [];
                                for (v = 0; v < r; v++) {
                                    C = u[v];
                                    if (C.checked) {
                                        y[y.length] = C.value;
                                    }
                                }
                                w[m] = y;
                                break;
                            }
                        }
                    }
                }
            }
            return w;
        },
        destroy: function (i) {
            d.call(this);
            this._aButtons = null;
            var j = this.element.getElementsByTagName("form"),
                k;
            if (j.length > 0) {
                k = j[0];
                if (k) {
                    b.purgeElement(k);
                    if (k.parentNode) {
                        k.parentNode.removeChild(k);
                    }
                    this.form = null;
                }
            }
            a.superclass.destroy.call(this, i);
        },
        toString: function () {
            return "Dialog " + this.id;
        }
    });
}());
(function () {
    YAHOO.widget.SimpleDialog = function (e, d) {
        YAHOO.widget.SimpleDialog.superclass.constructor.call(this, e, d);
    };
    var c = YAHOO.util.Dom,
        b = YAHOO.widget.SimpleDialog,
        a = {
            "ICON": {
                key: "icon",
                value: "none",
                suppressEvent: true
            },
            "TEXT": {
                key: "text",
                value: "",
                suppressEvent: true,
                supercedes: ["icon"]
            }
        };
    b.ICON_BLOCK = "blckicon";
    b.ICON_ALARM = "alrticon";
    b.ICON_HELP = "hlpicon";
    b.ICON_INFO = "infoicon";
    b.ICON_WARN = "warnicon";
    b.ICON_TIP = "tipicon";
    b.ICON_CSS_CLASSNAME = "yui-icon";
    b.CSS_SIMPLEDIALOG = "yui-simple-dialog";
    YAHOO.extend(b, YAHOO.widget.Dialog, {
        initDefaultConfig: function () {
            b.superclass.initDefaultConfig.call(this);
            this.cfg.addProperty(a.ICON.key, {
                handler: this.configIcon,
                value: a.ICON.value,
                suppressEvent: a.ICON.suppressEvent
            });
            this.cfg.addProperty(a.TEXT.key, {
                handler: this.configText,
                value: a.TEXT.value,
                suppressEvent: a.TEXT.suppressEvent,
                supercedes: a.TEXT.supercedes
            });
        },
        init: function (e, d) {
            b.superclass.init.call(this, e);
            this.beforeInitEvent.fire(b);
            c.addClass(this.element, b.CSS_SIMPLEDIALOG);
            this.cfg.queueProperty("postmethod", "manual");
            if (d) {
                this.cfg.applyConfig(d, true);
            }
            this.beforeRenderEvent.subscribe(function () {
                if (!this.body) {
                    this.setBody("");
                }
            }, this, true);
            this.initEvent.fire(b);
        },
        registerForm: function () {
            b.superclass.registerForm.call(this);
            var e = this.form.ownerDocument,
                d = e.createElement("input");
            d.type = "hidden";
            d.name = this.id;
            d.value = "";
            this.form.appendChild(d);
        },
        configIcon: function (k, j, h) {
            var d = j[0],
                e = this.body,
                f = b.ICON_CSS_CLASSNAME,
                l, i, g;
            if (d && d != "none") {
                l = c.getElementsByClassName(f, "*", e);
                if (l.length === 1) {
                    i = l[0];
                    g = i.parentNode;
                    if (g) {
                        g.removeChild(i);
                        i = null;
                    }
                }
                if (d.indexOf(".") == -1) {
                    i = document.createElement("span");
                    i.className = (f + " " + d);
                    i.innerHTML = "&#160;";
                } else {
                    i = document.createElement("img");
                    i.src = (this.imageRoot + d);
                    i.className = f;
                }
                if (i) {
                    e.insertBefore(i, e.firstChild);
                }
            }
        },
        configText: function (e, d, f) {
            var g = d[0];
            if (g) {
                this.setBody(g);
                this.cfg.refireEvent("icon");
            }
        },
        toString: function () {
            return "SimpleDialog " + this.id;
        }
    });
}());
(function () {
    YAHOO.widget.ContainerEffect = function (e, h, g, d, f) {
        if (!f) {
            f = YAHOO.util.Anim;
        }
        this.overlay = e;
        this.attrIn = h;
        this.attrOut = g;
        this.targetElement = d || e.element;
        this.animClass = f;
    };
    var b = YAHOO.util.Dom,
        c = YAHOO.util.CustomEvent,
        a = YAHOO.widget.ContainerEffect;
    a.FADE = function (d, f) {
        var g = YAHOO.util.Easing,
            i = {
                attributes: {
                    opacity: {
                        from: 0,
                        to: 1
                    }
                },
                duration: f,
                method: g.easeIn
            },
            e = {
                attributes: {
                    opacity: {
                        to: 0
                    }
                },
                duration: f,
                method: g.easeOut
            },
            h = new a(d, i, e, d.element);
        h.handleUnderlayStart = function () {
            var k = this.overlay.underlay;
            if (k && YAHOO.env.ua.ie) {
                var j = (k.filters && k.filters.length > 0);
                if (j) {
                    b.addClass(d.element, "yui-effect-fade");
                }
            }
        };
        h.handleUnderlayComplete = function () {
            var j = this.overlay.underlay;
            if (j && YAHOO.env.ua.ie) {
                b.removeClass(d.element, "yui-effect-fade");
            }
        };
        h.handleStartAnimateIn = function (k, j, l) {
            l.overlay._fadingIn = true;
            b.addClass(l.overlay.element, "hide-select");
            if (!l.overlay.underlay) {
                l.overlay.cfg.refireEvent("underlay");
            }
            l.handleUnderlayStart();
            l.overlay._setDomVisibility(true);
            b.setStyle(l.overlay.element, "opacity", 0);
        };
        h.handleCompleteAnimateIn = function (k, j, l) {
            l.overlay._fadingIn = false;
            b.removeClass(l.overlay.element, "hide-select");
            if (l.overlay.element.style.filter) {
                l.overlay.element.style.filter = null;
            }
            l.handleUnderlayComplete();
            l.overlay.cfg.refireEvent("iframe");
            l.animateInCompleteEvent.fire();
        };
        h.handleStartAnimateOut = function (k, j, l) {
            l.overlay._fadingOut = true;
            b.addClass(l.overlay.element, "hide-select");
            l.handleUnderlayStart();
        };
        h.handleCompleteAnimateOut = function (k, j, l) {
            l.overlay._fadingOut = false;
            b.removeClass(l.overlay.element, "hide-select");
            if (l.overlay.element.style.filter) {
                l.overlay.element.style.filter = null;
            }
            l.overlay._setDomVisibility(false);
            b.setStyle(l.overlay.element, "opacity", 1);
            l.handleUnderlayComplete();
            l.overlay.cfg.refireEvent("iframe");
            l.animateOutCompleteEvent.fire();
        };
        h.init();
        return h;
    };
    a.SLIDE = function (f, d) {
        var i = YAHOO.util.Easing,
            l = f.cfg.getProperty("x") || b.getX(f.element),
            k = f.cfg.getProperty("y") || b.getY(f.element),
            m = b.getClientWidth(),
            h = f.element.offsetWidth,
            j = {
                attributes: {
                    points: {
                        to: [l, k]
                    }
                },
                duration: d,
                method: i.easeIn
            },
            e = {
                attributes: {
                    points: {
                        to: [(m + 25), k]
                    }
                },
                duration: d,
                method: i.easeOut
            },
            g = new a(f, j, e, f.element, YAHOO.util.Motion);
        g.handleStartAnimateIn = function (o, n, p) {
            p.overlay.element.style.left = ((-25) - h) + "px";
            p.overlay.element.style.top = k + "px";
        };
        g.handleTweenAnimateIn = function (q, p, r) {
            var s = b.getXY(r.overlay.element),
                o = s[0],
                n = s[1];
            if (b.getStyle(r.overlay.element, "visibility") == "hidden" && o < l) {
                r.overlay._setDomVisibility(true);
            }
            r.overlay.cfg.setProperty("xy", [o, n], true);
            r.overlay.cfg.refireEvent("iframe");
        };
        g.handleCompleteAnimateIn = function (o, n, p) {
            p.overlay.cfg.setProperty("xy", [l, k], true);
            p.startX = l;
            p.startY = k;
            p.overlay.cfg.refireEvent("iframe");
            p.animateInCompleteEvent.fire();
        };
        g.handleStartAnimateOut = function (o, n, r) {
            var p = b.getViewportWidth(),
                s = b.getXY(r.overlay.element),
                q = s[1];
            r.animOut.attributes.points.to = [(p + 25), q];
        };
        g.handleTweenAnimateOut = function (p, o, q) {
            var s = b.getXY(q.overlay.element),
                n = s[0],
                r = s[1];
            q.overlay.cfg.setProperty("xy", [n, r], true);
            q.overlay.cfg.refireEvent("iframe");
        };
        g.handleCompleteAnimateOut = function (o, n, p) {
            p.overlay._setDomVisibility(false);
            p.overlay.cfg.setProperty("xy", [l, k]);
            p.animateOutCompleteEvent.fire();
        };
        g.init();
        return g;
    };
    a.prototype = {
        init: function () {
            this.beforeAnimateInEvent = this.createEvent("beforeAnimateIn");
            this.beforeAnimateInEvent.signature = c.LIST;
            this.beforeAnimateOutEvent = this.createEvent("beforeAnimateOut");
            this.beforeAnimateOutEvent.signature = c.LIST;
            this.animateInCompleteEvent = this.createEvent("animateInComplete");
            this.animateInCompleteEvent.signature = c.LIST;
            this.animateOutCompleteEvent = this.createEvent("animateOutComplete");
            this.animateOutCompleteEvent.signature = c.LIST;
            this.animIn = new this.animClass(this.targetElement, this.attrIn.attributes, this.attrIn.duration, this.attrIn.method);
            this.animIn.onStart.subscribe(this.handleStartAnimateIn, this);
            this.animIn.onTween.subscribe(this.handleTweenAnimateIn, this);
            this.animIn.onComplete.subscribe(this.handleCompleteAnimateIn, this);
            this.animOut = new this.animClass(this.targetElement, this.attrOut.attributes, this.attrOut.duration, this.attrOut.method);
            this.animOut.onStart.subscribe(this.handleStartAnimateOut, this);
            this.animOut.onTween.subscribe(this.handleTweenAnimateOut, this);
            this.animOut.onComplete.subscribe(this.handleCompleteAnimateOut, this);
        },
        animateIn: function () {
            this._stopAnims(this.lastFrameOnStop);
            this.beforeAnimateInEvent.fire();
            this.animIn.animate();
        },
        animateOut: function () {
            this._stopAnims(this.lastFrameOnStop);
            this.beforeAnimateOutEvent.fire();
            this.animOut.animate();
        },
        lastFrameOnStop: true,
        _stopAnims: function (d) {
            if (this.animOut && this.animOut.isAnimated()) {
                this.animOut.stop(d);
            }
            if (this.animIn && this.animIn.isAnimated()) {
                this.animIn.stop(d);
            }
        },
        handleStartAnimateIn: function (e, d, f) {},
        handleTweenAnimateIn: function (e, d, f) {},
        handleCompleteAnimateIn: function (e, d, f) {},
        handleStartAnimateOut: function (e, d, f) {},
        handleTweenAnimateOut: function (e, d, f) {},
        handleCompleteAnimateOut: function (e, d, f) {},
        toString: function () {
            var d = "ContainerEffect";
            if (this.overlay) {
                d += " [" + this.overlay.toString() + "]";
            }
            return d;
        }
    };
    YAHOO.lang.augmentProto(a, YAHOO.util.EventProvider);
})();
YAHOO.register("container", YAHOO.widget.Module, {
    version: "2.9.0",
    build: "2800"
});