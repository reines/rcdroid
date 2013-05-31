;(function ( $, window, document, undefined ) {

    // Create the defaults once
    var pluginName = 'cube',
        defaults = {
            color: '#000000',
            x: 0,
            y: 0,
            z: 0
        };

    var self;

    // The actual plugin constructor
    function Plugin( element, options ) {
        self = this;

        this.element = element;

        this.options = $.extend( {}, defaults, options );

        this._defaults = defaults;
        this._name = pluginName;

        this.init();
    }

    Plugin.prototype = {
        init: function() {
            this.ctx = this.element.getContext('2d');

            this.width = $(this.element).width();
            this.height = $(this.element).height();

            setInterval(render, 33);
        },

        rotate: function(x, y, z) {
            this.options.x = x;
            this.options.y = y;
            this.options.z = z;
        }
    };

    function Point3D(x,y,z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.rotateX = function(angle) {
            var rad, cosa, sina, y, z
            rad = angle * Math.PI / 180
            cosa = Math.cos(rad)
            sina = Math.sin(rad)
            y = this.y * cosa - this.z * sina
            z = this.y * sina + this.z * cosa
            return new Point3D(this.x, y, z)
        }

        this.rotateY = function(angle) {
            var rad, cosa, sina, x, z
            rad = angle * Math.PI / 180
            cosa = Math.cos(rad)
            sina = Math.sin(rad)
            z = this.z * cosa - this.x * sina
            x = this.z * sina + this.x * cosa
            return new Point3D(x,this.y, z)
        }

        this.rotateZ = function(angle) {
            var rad, cosa, sina, x, y
            rad = angle * Math.PI / 180
            cosa = Math.cos(rad)
            sina = Math.sin(rad)
            x = this.x * cosa - this.y * sina
            y = this.x * sina + this.y * cosa
            return new Point3D(x, y, this.z)
        }

        this.project = function(viewWidth, viewHeight, fov, viewDistance) {
            var factor, x, y
            factor = fov / (viewDistance + this.z)
            x = this.x * factor + viewWidth / 2
            y = this.y * factor + viewHeight / 2
            return new Point3D(x, y, this.z)
        }
    }

    var vertices = [
        new Point3D(-1, 1, -1),
        new Point3D(1, 1, -1),
        new Point3D(1, -1, -1),
        new Point3D(-1, -1, -1),
        new Point3D(-1, 1, 1),
        new Point3D(1, 1, 1),
        new Point3D(1, -1, 1),
        new Point3D(-1, -1, 1)
    ];

    // Define the vertices that compose each of the 6 faces. These numbers are
    // indices to the vertex list defined above.
    var faces = [[0,1,2,3], [1,5,6,2], [5,4,7,6], [4,0,3,7], [0,4,5,1], [3,2,6,7]]

    function render() {
        var ctx = self.ctx;
        var t = new Array();

        ctx.fillStyle = 'white';
        ctx.fillRect(0, 0, self.width, self.height);

        for (var i = 0; i < vertices.length; i++) {
            var vertex = vertices[i];
            var r = vertex.rotateX(self.options.x).rotateY(self.options.y).rotateZ(self.options.z);
            var p = r.project(self.width, self.height, 128, 3.5);

            t.push(p);
        }

        ctx.strokeStyle = self.options.color;

        for (var i = 0; i < faces.length; i++) {
            var face = faces[i];

            ctx.beginPath()

            ctx.moveTo(t[face[0]].x, t[face[0]].y)

            ctx.lineTo(t[face[1]].x, t[face[1]].y)
            ctx.lineTo(t[face[2]].x, t[face[2]].y)
            ctx.lineTo(t[face[3]].x, t[face[3]].y)

            ctx.closePath()
            ctx.stroke()
        }
    }

    // A really lightweight plugin wrapper around the constructor,
    // preventing against multiple instantiations
    $.fn[pluginName] = function ( options ) {
        var isMethodCall = typeof options === 'string';
        var args = Array.prototype.slice.call(arguments, 1);

        return this.each(function () {
            var instance = $.data(this, 'plugin_' + pluginName);
            if (!instance)
                instance = $.data(this, 'plugin_' + pluginName, new Plugin( this, options ));

            if (isMethodCall) {
                instance[options].apply(instance, args);
            }
        });
    };

})( jQuery, window, document );
