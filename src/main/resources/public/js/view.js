/**
 * Radius of Pac-Man is 20px.
 */
let app;
let keyCmd = 0;
let intervalId;
let intervalId4DiedMan;
let intervalId4Level;
let isPause = false;
let highScore = parseInt(localStorage.getItem('hs'));
let id;
let lastLives;
let lastX = 620;
let lastY = 300;
let lastDirX;
let lastDirY;
let mapId;

if (!lastLives) {
    lastLives = 2;
}

if (!highScore) {
    highScore = 0;
    localStorage.setItem('hs', highScore);
}

let createApp = (canvas) => {
    let c = canvas.getContext('2d');
    let freqMan = true;
    let lastX = 620;
    let lastY = 300;
    let drawMan = (x, y, radius, dirX, dirY) => {

        if (freqMan) {
            c.fillStyle = 'yellow';
            c.beginPath();
            c.arc(x, y, radius - 4, 0, 2 * Math.PI, false);
            c.closePath();
            c.fill();
        } else {
            if (dirX === 1) {
                c.beginPath();
                c.arc(x, y, radius - 4, 0.25 * Math.PI, 1.25 * Math.PI, false);
                c.fillStyle = "rgb(255, 255, 0)";
                c.fill();
                c.beginPath();
                c.arc(x, y, radius - 4, 0.75 * Math.PI, 1.75 * Math.PI, false);
                c.fill();
            } else if (dirY === 1) {
                c.beginPath();
                c.arc(x, y, radius - 4, 0.75 * Math.PI, 1.75 * Math.PI, false);
                c.fillStyle = "rgb(255, 255, 0)";
                c.fill();
                c.beginPath();
                c.arc(x, y, radius - 4, 1.25 * Math.PI, 0.25 * Math.PI, false);
                c.fill();
            } else if (dirX === -1) {
                c.beginPath();
                c.arc(x, y, radius - 4, -0.25 * Math.PI, 0.75 * Math.PI, false);
                c.fillStyle = "rgb(255, 255, 0)";
                c.fill();
                c.beginPath();
                c.arc(x, y, radius - 4, 1.25 * Math.PI, 2.25 * Math.PI, false);
                c.fill();
            } else if (dirY === -1) {
                c.beginPath();
                c.arc(x, y, radius - 4, -0.25 * Math.PI, 0.75 * Math.PI, false);
                c.fillStyle = "rgb(255, 255, 0)";
                c.fill();
                c.beginPath();
                c.arc(x, y, radius - 4, 0.25 * Math.PI, 1.25 * Math.PI, false);
                c.fill();
            }
        }
        // if (lastY !== y || lastX !== x) {
        freqMan = !freqMan;
        // }
        lastX = x;
        lastY = y;
    };

    let showGrid = () => {
        for (let i = 0; i < canvas.height; i += 20) {
            c.strokeStyle = 'green';
            c.beginPath();
            c.moveTo(0, i);
            c.lineTo(canvas.width, i);
            c.closePath();
            c.stroke();
            c.fillStyle = 'white';
            c.font = '10px Arial';
            c.fillText('' + i / 20, 0, i);
        }
        for (let i = 0; i < canvas.width; i += 20) {
            c.strokeStyle = 'green';
            c.beginPath();
            c.moveTo(i, 0);
            c.lineTo(i, canvas.height);
            c.closePath();
            c.stroke();
            c.fillStyle = 'white';
            c.font = '10px Arial';
            c.fillText('' + i / 20, i, 10);
        }
    };

    let drawBackground = () => {
        c.fillStyle = '#000000';
        c.beginPath();
        c.rect(0, 0, canvas.width, canvas.height);
        c.closePath();
        c.fill();
        //showGrid();
    };
    let drawWall = (s, cl) => {
        let bx = s.split('-')[0] * 20, by = s.split('-')[1] * 20, ex = s.split('-')[2] * 20, ey = s.split('-')[3] * 20;
        let colors = ['blue', 'red', 'white', 'green'];
        c.strokeStyle = colors[Math.floor(Math.random() * 4)];
        c.strokeStyle = cl;
        c.lineWidth = 5;
        c.beginPath();
        c.moveTo(bx, by);
        c.lineTo(ex, ey);
        c.closePath();
        c.stroke();
        c.strokeStyle = "black";
        c.lineWidth = 1;
        c.beginPath();
        c.moveTo(bx, by);
        c.lineTo(ex, ey);
        c.closePath();
        c.stroke();
    };

    let drawMonster = (x, y, dirX, dirY, cl) => {
        let loc = "";
        loc += cl;
        if (dirX === 1) {
            loc += "Right";
        } else if (dirX === -1) {
            loc += "Left";
        } else if (dirY === -1) {
            loc += "Up";
        } else if (dirY === 1) {
            loc += "Down";
        }
        if (freqMan) {
            loc += "1";
        } else {
            loc += "2";
        }
        let img = document.getElementById(loc);
        let width = img.width;
        let height = img.height;
        c.translate(x, y);
        c.drawImage(img, 0, 0, width, height, -18, -18, 36, 36);
        c.translate(-x, -y);
    };

    let drawBlue = (x, y) => {
        let loc = "flashB1";
        if (freqMan) {
            loc = "flashB2";
        }
        let img = document.getElementById(loc);
        let width = img.width;
        let height = img.height;
        c.translate(x, y);
        c.drawImage(img, 0, 0, width, height, -18, -18, 36, 36);
        c.translate(-x, -y);
    };

    let drawFlash = (x, y) => {
        let loc = "flashW1";
        if (freqMan) {
            loc = "flashB2";
        }
        let img = document.getElementById(loc);
        let width = img.width;
        let height = img.height;
        c.translate(x, y);
        c.drawImage(img, 0, 0, width, height, -18, -18, 36, 36);
        c.translate(-x, -y);
    };

    let drawDie = (x, y, dirX, dirY) => {
        let loc = "eyeB";
        if (dirX === 1) {
            loc += "Right";
        } else if (dirX === -1) {
            loc += "Left";
        } else if (dirY === -1) {
            loc += "Up";
        } else if (dirY === 1) {
            loc += "Down";
        }
        let img = document.getElementById(loc);
        let width = img.width;
        let height = img.height;
        c.translate(x, y);
        c.drawImage(img, 0, 0, width, height, -18, -18, 36, 36);
        c.translate(-x, -y);
    };

    let drawPac = (s) => {
        let x = s.split('-')[0] * 20, y = s.split('-')[1] * 20, radius = s.split('-')[2];
        if (parseInt(radius) === 10 && freqMan) {
            c.fillStyle = "pink";
            c.beginPath();
            c.arc(x, y, radius, 0, 2 * Math.PI, false);
            c.closePath();
            c.fill();
        } else if (parseInt(radius) !== 10) {
            c.fillStyle = "pink";
            c.beginPath();
            c.arc(x, y, radius, 0, 2 * Math.PI, false);
            c.closePath();
            c.fill();
        }
    };

    let drawScore = (x, y, score) => {
        c.fillStyle = "white";
        c.font = "20px Comic Sans MS";
        c.textAlign = "center";
        c.fillText(score.toString(), x, y + 5);
    };

    let drawDieMan = (x, y, dirX, dirY, data) => {
        data.sort(function(a, b) {
            if (a.type === 'ghost' && b.type !== 'ghost') {
                return 1;
            }
        });
        let a = 0;
        if (dirY === 1) {
            a = Math.PI / 2;
        } else if (dirX === -1) {
            a = Math.PI;
        } else if (dirY === -1) {
            a = -Math.PI / 2;
        }
        let i = 0;
        intervalId4DiedMan = setInterval(function () {
            i += Math.PI / 8;
            if (i > Math.PI) {
                clearInterval(intervalId4DiedMan);
                intervalId4DiedMan = null;
            }
            drawDieManHelper(data, x, y, i, a);

        }, 100);


    };

    let drawDieManHelper = (data, x, y, i, a) => {
        if (i > Math.PI) {
            return;
        }
        app.clear();
        app.drawBackground();
        for (let obj of data) {

            if (obj.type === 'man') {
                //Draw
                c.fillStyle = "yellow";
                c.beginPath();
                c.moveTo(x, y);
                c.arc(x, y, 17, i + a, 2 * Math.PI - i + a);
                c.closePath();
                c.fill();

                let sc = '000000' + obj.score.toString();
                $('#scoreNum').html(sc.substr(sc.length - 6, sc.length - 1));
                if (highScore < obj.score) {
                    let hsc = '000000' + obj.score.toString();
                    $('#highScoreNum').html(hsc.substr(hsc.length - 6, hsc.length - 1));
                    highScore = obj.score;
                    localStorage.setItem('hs', highScore);
                } else {
                    let hsc = '000000' + highScore.toString();
                    $('#highScoreNum').html(hsc.substr(hsc.length - 6, hsc.length - 1));
                }
                $('#lifeNum').html(obj.lives > 0 ? obj.lives.toString() : '0');
                $('#levelNum').html(obj.level > 0 ? obj.level.toString() : '0');
            } else if (obj.type === 'wall') {
                app.drawWall(obj.p, obj.color);
            } else if (obj.type === 'pac' && obj.p.split('-')[3] !== 1) {
                app.drawPac(obj.p);
            }
        }
    };

    let drawGameOver = (data) => {
        app.clear();
        app.drawBackground();
        for (let obj of data) {
            if (obj.type === 'wall') {
                app.drawWall(obj.p, obj.color);
            } else if (obj.type === 'pac' && obj.p.split('-')[3] !== 1) {
                app.drawPac(obj.p);
            }
        }
        $("#logoOver").show();
    };

    let drawFruit = (x, y, name) => {
        let loc = "";
        if (name === "pineapple"){
            loc = "pineapple";
        } else if(name === "banana"){
            loc = "banana";
        } else if(name === "durian"){
            loc = "durian";
        } else if(name === "strawberry"){
            loc = "strawberry";
        } else if(name === "watermelon"){
            loc = "watermelon";
        }

        let img = document.getElementById(loc);
        let width = img.width;
        let height = img.height;
        c.translate(x, y);
        c.drawImage(img, 0, 0, width, height, -18, -18, 36, 36);
        c.translate(-x, -y);
    };

    let drawLevel = (l) => {
        let s = "ENTERED LEVEL " + l;
        let i = 0;
        $('#levelNoti').html(s);
        $('#levelNoti').show();
        intervalId4Level = setInterval(function () {
            if (i < 15 && i % 2) {
                $('#levelNoti').css("color", "red");
            } else if (i < 15 && (i % 2 === 0)) {
                $('#levelNoti').css("color", "white");
            } else {
                $('#levelNoti').hide();
                i = 0;
                clearInterval(intervalId4Level);
                intervalId4Level = null;
            }
            i++;
        }, 100);
    };

    let clear = () => {
        c.clearRect(0, 0, canvas.width, canvas.height);
    };

    return {
        drawMan: drawMan,
        drawMonster: drawMonster,
        drawFlash: drawFlash,
        drawBlue: drawBlue,
        drawDie: drawDie,
        drawScore: drawScore,
        drawDieMan: drawDieMan,
        drawGameOver: drawGameOver,
        drawLevel,
        drawFruit,
        drawBackground,
        drawWall,
        drawPac,
        clear
    }
};


let resume = () => {
    if (!intervalId) {
        clearInterval(intervalId);
        intervalId = setInterval(update, 150);
    }
};

let update = () => {
    if (!isPause) {
        $.get('/update', {cmd: keyCmd, id: id}, data => {
            data.sort(function(a, b) {
                if (a.type === 'ghost' && b.type !== 'ghost') {
                    return 1;
                } else if (a.type !== 'ghost' && b.type === 'ghost'){
                    return -1;
                }
            });
            app.clear();
            app.drawBackground();
            for (let obj of data) {
                if (obj.type === 'man') {
                    app.drawMan(obj.loc.x, obj.loc.y, obj.radius, obj.dir.x, obj.dir.y);
                    let sc = '000000' + obj.score.toString();
                    $('#scoreNum').html(sc.substr(sc.length - 6, sc.length - 1));
                    if (highScore < obj.score) {
                        let hsc = '000000' + obj.score.toString();
                        $('#highScoreNum').html(hsc.substr(hsc.length - 6, hsc.length - 1));
                        highScore = obj.score;
                        localStorage.setItem('hs', highScore);
                    } else {
                        let hsc = '000000' + highScore.toString();
                        $('#highScoreNum').html(hsc.substr(hsc.length - 6, hsc.length - 1));
                    }
                    if (obj.lives !== lastLives) {
                        clearInterval(intervalId);
                        intervalId = null;
                        lastLives = obj.lives;
                        app.drawDieMan(lastX, lastY, lastDirX, lastDirY, data);
                        keyCmd = 0;
                        if (obj.lives < 0) {
                            app.drawGameOver(data);
                        } else {
                            setTimeout(resume, 1500);
                        }
                    }
                    $('#lifeNum').html(obj.lives > 0 ? obj.lives.toString() : '0');
                    $('#dotsEatenNum').html(obj.eatenNum > 0 ? obj.eatenNum.toString() : '0');
                    $('#levelNum').html(obj.level > 0 ? obj.level.toString() : '0');
                    if (obj.nextLevel === true){
                        app.drawLevel(obj.level > 0 ? obj.level.toString() : '0');
                    }
                    if (obj.loc.x !== 620 && obj.loc.y !== 300) {
                        lastX = obj.loc.x;
                        lastY = obj.loc.y;
                        lastDirX = obj.dir.x;
                        lastDirY = obj.dir.y;
                    }
                } else if (obj.type === 'wall') {
                    app.drawWall(obj.p, obj.color);
                } else if (obj.type === 'pac' && obj.p.split('-')[3] !== 1) {
                    app.drawPac(obj.p);
                } else if (obj.type === 'ghost') {
                    if (obj.dieScore === 0) {
                        if (obj.isStayAway) {
                            app.drawBlue(obj.loc.x, obj.loc.y);
                        } else if (obj.isFlashing) {
                            app.drawFlash(obj.loc.x, obj.loc.y);
                        } else if (obj.isDead) {
                            app.drawDie(obj.loc.x, obj.loc.y, obj.dir.x, obj.dir.y);
                        } else {
                            app.drawMonster(obj.loc.x, obj.loc.y, obj.dir.x, obj.dir.y, obj.color);
                        }
                    } else {
                        app.drawScore(obj.loc.x, obj.loc.y, obj.dieScore);
                        clearInterval(intervalId);
                        intervalId = null;
                        setTimeout(resume, 500);
                    }
                } else if(obj.type === 'fruit') {
                    let s = obj.p;
                    app.drawFruit(s.split('-')[0] * 20, s.split('-')[1] * 20, obj.fruitName);
                }
            }
        }, 'json');
        $("#logoPause").hide();
    }
};

let handleSwitcherClick = () => {
    let cd = $('#pacman-body').css('display');
    if(cd !== 'none') {
        if(!isPause) pause();
    }
    $('#pacman-body').css('display', cd === 'none' ? 'initial' : 'none');
    cd = $('#map-editor').css('display');
    $('#map-editor').css('display', cd === 'none' ? 'initial' : 'none');

};


$(document).ready(() => {
    $('#switchMap').multiselect({
        multiple: false,
        onChange: function () {
            if (this.$select.val() === 'db') {
                mapId = 0;
            } else if (this.$select.val() === 'cxk') {
                mapId = 1;
            }
        }
    });
    reset(true).then(function () {
        app = createApp(document.querySelector('canvas'));
        $('body').keydown(e => {
            keyCmd = parseInt(e.keyCode ? e.keyCode : e.which);
            if (isPause) {
                isPause = !isPause;
            }
        });
        $("#pause").click(function () {
            pause()
        });
        $("#start").click(function () {
            reset(false);
            $("#logoOver").hide();
            clearInterval(intervalId);
            intervalId = setInterval(update, 150);
        });
        $("#switcher").click(handleSwitcherClick);

        $('#play-customized').click(function () {
            id = uuidv4();
            let mapJson = generate();
            $.post('/restartCustomized', JSON.stringify({id: id, map: mapJson}), (data) => {
                handleSwitcherClick();
                app.clear();
                if(isPause) pause();
            }, "json")
        });
        jQuery.ajaxSetup({async: false});
        update();
        jQuery.ajaxSetup({async: true});

    }).then(function () {
        clearInterval(intervalId);
        intervalId = setInterval(update, 150);
    });
});

window.onbeforeunload = function (e) {
    quit();
    return 'Leave the game?';
};

function quit() {
    $.get('/quit', {id: id}, data => {
        console.log("quit");
    });
}

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function pause() {
    $("#logoPause").show();
    isPause = !isPause;
}

function reset(flag) {
    id = uuidv4();
    return $.get('/restart', {id: id, mapId: mapId}, data => {
        keyCmd = 0;
        lastLives = 2;
        console.log("ok");
    });
}



