let color = 'red';
let type = 'wall';
let tunnel1 = null, tunnel2 = null;
let w = 58, h = 36;
let handleSetColor = () => {
    color = $('#color-input').val();
    $('#current-color').html(color);
    $('#current-color').css("color", color);
};
let changeState = (grid, flag, passable = false) => {
    if (flag === 2) {
        $(grid).prop('selh', !$(grid).prop('selh'));
        if ($(grid).prop('selh') === true) {
            let c = color;
            if (type === 'pac') {
                c = 'pink';
            } else if (type === 'ghost-wall') {
                c = 'green';
            }
            $(grid).css('border-top', c + ' solid 3px');
            $(grid).prop('colorh', c);
            $(grid).prop('typeh', type === 'pac' ? 'pac' : 'wall');
            $(grid).prop('passableh', passable);
        } else {
            $(grid).css('border-top', 'none');
        }
    } else {
        $(grid).prop('selv', !$(grid).prop('selv'));
        if ($(grid).prop('selv') === true) {
            let c = color;
            if (type === 'pac') {
                c = 'pink';
            } else if (type === 'ghost-wall') {
                c = 'green';
            }
            $(grid).css('border-left', c + ' solid 3px');
            $(grid).prop('colorv', c);
            $(grid).prop('typev', type === 'pac' ? 'pac' : 'wall');
            $(grid).prop('passablev', passable);
        } else {
            $(grid).css('border-left', 'none');
        }
    }
};
let handleClick = (e) => {
    let beginGrid = e.target;
    let lastGrid = e.target;
    let flag = 0;
    $('.grid').unbind();
    $('.grid').mouseover((e) => {
        let curGrid = e.target;
        if (flag !== 2 && $(curGrid).prop('col') === $(beginGrid).prop('col')) {
            if (Math.abs($(curGrid).prop('row') - $(lastGrid).prop('row')) === 1) {
                if (flag === 0) {
                    flag = 1;
                    changeState(beginGrid, flag, type === 'ghost-wall');
                }
                changeState(curGrid, flag, type === 'ghost-wall');
                lastGrid = curGrid;
            }
        } else if (flag !== 1 && $(curGrid).prop('row') === $(beginGrid).prop('row')) {
            if (Math.abs($(curGrid).prop('col') - $(lastGrid).prop('col')) === 1) {
                if (flag === 0) {
                    flag = 2;
                    changeState(beginGrid, flag, type === 'ghost-wall');
                }
                changeState(curGrid, flag, type === 'ghost-wall');
                lastGrid = curGrid;
            }
        }
    })
    $('.grid').click((e) => {
        $('.grid').unbind();
        $('.grid').click(handleClick);
    })
};
let generate = () => {
    let walls = [];
    let pacs = [];
    let unitSz = 20;
    //horizontal
    for (let i = 0; i < h; i++) {
        for (let j = 0; j < w; j++) {
            if ($('#' + i + '-' + j).prop('selh') === true) {
                let startPos = [i, j];
                let endPos = [i, j];
                let c = $('#' + i + '-' + j).prop('colorh');
                let t = $('#' + i + '-' + j).prop('typeh');
                let p = $('#' + i + '-' + j).prop('passableh');
                while (j + 1 < w && $('#' + i + '-' + (j + 1)).prop('selh') === true && $('#' + i + '-' + (j + 1)).prop('colorh') === c
                && $('#' + i + '-' + (j + 1)).prop('typeh') === t && $('#' + i + '-' + (j + 1)).prop('passableh') === p) {
                    j++;
                    endPos = [i, j];
                }
                let e = {
                    start: [startPos[1] * unitSz, startPos[0] * unitSz],
                    end: [endPos[1] * unitSz + unitSz, endPos[0] * unitSz],
                };
                if (t === 'wall') {
                    e.color = c;
                    if (p === true) {
                        e.passable = true;
                    }
                    walls.push(e);
                } else if (t === 'pac') {
                    pacs.push(e);
                }
            }
        }
    }

    //vertical
    for (let j = 0; j < w; j++) {
        for (let i = 0; i < h; i++) {
            if ($('#' + i + '-' + j).prop('selv') === true) {
                let startPos = [i, j];
                let endPos = [i, j];
                let c = $('#' + i + '-' + j).prop('colorv');
                let t = $('#' + i + '-' + j).prop('typev');
                let p = $('#' + i + '-' + j).prop('passablev');

                while (i + 1 < h && $('#' + (i + 1) + '-' + j).prop('selv') === true && $('#' + (i + 1) + '-' + j).prop('colorv') === c
                && $('#' + (i + 1) + '-' + j).prop('typev') === t && $('#' + (i + 1) + '-' + j).prop('passablev') === p) {
                    i++;
                    endPos = [i, j];
                }
                let e = {
                    start: [startPos[1] * unitSz, startPos[0] * unitSz],
                    end: [endPos[1] * unitSz, endPos[0] * unitSz + unitSz],
                };
                if (t === 'wall') {
                    e.color = c;
                    if (p === true) {
                        e.passable = true;
                    }
                    walls.push(e);
                } else if (t === 'pac') {
                    pacs.push(e);
                }
            }
        }
    }

    walls.push({
        start: [0 * unitSz, 0 * unitSz],
        end: [w * unitSz + unitSz, 0 * unitSz],
        color: 'red'
    });
    walls.push({
        start: [0 * unitSz, h * unitSz],
        end: [w * unitSz + unitSz, h * unitSz],
        color: 'red'

    });
    walls.push({
        start: [0 * unitSz, 0 * unitSz],
        end: [0 * unitSz, h * unitSz + unitSz],
        color: 'red'

    });
    walls.push({
        start: [w * unitSz, 0 * unitSz],
        end: [w * unitSz, h * unitSz + unitSz],
        color: 'red'

    });

    let bigPacs = [];
    for (let i = 0; i < h; i++) {
        for (let j = 0; j < w; j++) {
            if ($('#' + i + '-' + j).prop('isEnergizer') === true) {
                bigPacs.push({
                    loc: [j * 20, (i + 1) * 20]
                })
            }
        }
    }

    //add tunnel
    if (tunnel1 !== null && tunnel2 !== null) {
        let i1 = parseInt(tunnel1.split('-')[0]), j1 = parseInt(tunnel1.split('-')[1]);
        let startPos1 = [i1, j1];
        let endPos1 = [i1 + 1, j1];
        let e1 = {
            start: [startPos1[1] * unitSz, startPos1[0] * unitSz],
            end: [endPos1[1] * unitSz, endPos1[0] * unitSz + unitSz],
        };
        e1.color = 'white';

        let i2 = parseInt(tunnel2.split('-')[0]), j2 = parseInt(tunnel2.split('-')[1]);
        let startPos2 = [i2, j2];
        let endPos2 = [i2 + 1, j2];
        let e2 = {
            start: [startPos2[1] * unitSz, startPos2[0] * unitSz],
            end: [endPos2[1] * unitSz, endPos2[0] * unitSz + unitSz],
        };
        e2.color = 'white';

        let deltaX = e2.start[0] - e1.start[0];
        let deltaY = e2.start[1] - e1.start[1];

        if(deltaX > 0) deltaX -= 2 * unitSz;
        else deltaX += 2 * unitSz;

        e1.teleport = [deltaX, deltaY];
        e2.teleport = [-deltaX, -deltaY];
        walls.push(e1);
        walls.push(e2);
    }


    return {wall: walls, pac: pacs, bigPac: bigPacs};
};
let download = (content, fileName, contentType) => {
    var a = document.createElement("a");
    var file = new Blob([content], {type: contentType});
    a.href = URL.createObjectURL(file);
    a.download = fileName;
    a.click();
};
let handleGenerate = () => {
    let json = generate();
    console.log(JSON.stringify(json));
    download(JSON.stringify(json), 'map.json', 'application/json');
};
let handleReset = () => {
    $('.grid').prop('selv', false);
    $('.grid').prop('selh', false);
    $('.grid').css('border-top', 'none');
    $('.grid').css('border-left', 'none');
    $('.grid').prop('isEnergizer', false);
    $('.grid').prop('passablev', false);
    $('.grid').prop('passableh', false);
    $('.grid').css('background-color', 'white');
    if (tunnel1 !== null) {
        setTunnel(1, 'white');
    }
    tunnel1 = null;
    if (tunnel2 !== null) {
        setTunnel(2, 'white');
    }
    tunnel2 = null;
};
let handleImport = (data) => {
    ghostWalls = [];
    tunnel1 = null;
    tunnel2 = null;
    console.log(data);
    let unitSz = 20;
    let initColor = color;
    let initType = type;

    let walls = data.wall;
    if (walls !== undefined) {
        for (let wall of walls) {
            if(wall.teleport !== undefined) {
                let r = wall.start[0] / unitSz;
                let c = Math.min(wall.start[1] / unitSz, wall.end[1] / unitSz);
                if(tunnel1 === null) {
                    tunnel1 = c + '-' + r;
                    setTunnel(1, 'grey');
                }
                else if(tunnel2 === null) {
                    tunnel2 = c + '-' + r;
                    setTunnel(2, 'grey');
                }
                continue;
            }
            if (wall.start[0] === wall.end[0]) {
                let r = wall.start[0] / unitSz;
                let cs = Math.min(wall.start[1] / unitSz, wall.end[1] / unitSz);
                let ce = Math.max(wall.start[1] / unitSz, wall.end[1] / unitSz) - 1;
                for (let c = cs; c <= ce; c++) {
                    color = wall.color;
                    type = 'wall';
                    if(wall.passable === 'true') {
                        console.log(r + '-' + c);
                    }
                    changeState($('#' + c + '-' + r), 1, wall.passable);
                }
            } else {
                let c = wall.start[1] / unitSz;
                let rs = Math.min(wall.start[0] / unitSz, wall.end[0] / unitSz);
                let re = Math.max(wall.start[0] / unitSz, wall.end[0] / unitSz) - 1;
                for (let r = rs; r <= re; r++) {
                    color = wall.color;
                    type = 'wall';
                    if(wall.passable === 'true') {
                        console.log(r + '-' + c);
                    }
                    changeState($('#' + c + '-' + r), 2, wall.passable);
                }
            }
        }
    }

    let pacs = data.pac;
    let set = [];
    if (pacs !== undefined) {
        for (let pac of pacs) {
            if (pac.start[0] === pac.end[0]) {
                let r = pac.start[0] / unitSz;
                let cs = Math.min(pac.start[1] / unitSz, pac.end[1] / unitSz);
                let ce = Math.max(pac.start[1] / unitSz, pac.end[1] / unitSz) - 1;
                for (let c = cs; c <= ce; c++) {
                    color = 'pink';
                    type = 'pac';
                    if (!set.includes('v-' + c + '-' + r)) {
                        changeState($('#' + c + '-' + r), 1);
                        set.push('v-' + c + '-' + r);
                    }

                }
            } else {

                let c = pac.start[1] / unitSz;
                let rs = Math.min(pac.start[0] / unitSz, pac.end[0] / unitSz);
                let re = Math.max(pac.start[0] / unitSz, pac.end[0] / unitSz) - 1;
                for (let r = rs; r <= re; r++) {
                    color = 'pink';
                    type = 'pac';
                    if (!set.includes('h-' + c + '-' + r)) {
                        changeState($('#' + c + '-' + r), 2);
                        set.push('h-' + c + '-' + r);
                    }
                }
            }
        }
    }

    let bigPacs = data.bigPac;
    if (bigPacs !== undefined) {
        for (let bigPac of bigPacs) {
            handleEnergizerClick({target: '#' + (bigPac.loc[1] / unitSz - 1) + '-' + (bigPac.loc[0] / unitSz)});
        }
    }


    color = initColor;
    type = initType;
};


let handleEnergizerClick = (e) => {
    let tar = e.target;
    let curState = $(tar).prop('isEnergizer');
    $(tar).prop('isEnergizer', !curState);
    if ($(tar).prop('isEnergizer')) {
        $(tar).css('background-color', 'pink');
    } else {
        $(tar).css('background-color', 'white');
    }
};

let setTunnel = (id, color) => {
    if (id === 1) {
        $('#' + tunnel1).css('background-color', color);
        $('#' + getCoupledId(tunnel1)).css('background-color', color);
    } else if (id === 2) {
        $('#' + tunnel2).css('background-color', color);
        $('#' + getCoupledId(tunnel2)).css('background-color', color);
    }
};

let getCoupledId = (id) => {
    return (parseInt(id.split('-')[0]) + 1) + '-' + id.split('-')[1];
};
let handleTunnelClick = (e) => {
    let tar = e.target;
    if (type === "tunnel-1") {
        if (tunnel1 !== null) {
            setTunnel(1, 'white');
        }
        tunnel1 = $(tar).attr('id');
        setTunnel(1, 'grey');
        if (tunnel2 !== null) {
            setTunnel(2, 'grey');
        }
    } else if (type === "tunnel-2") {
        if (tunnel2 !== null) {
            setTunnel(2, 'white');
        }
        tunnel2 = $(tar).attr('id');
        setTunnel(2, 'grey');
        if (tunnel1 !== null) {
            setTunnel(1, 'grey');
        }
    }
};

$(document).ready(() => {
    $('#switch-type').multiselect({
        multiple: false,
        onChange: function () {
            $('.grid').unbind();
            if (this.$select.val() === 'energizer') {
                $('.grid').click(handleEnergizerClick);
            } else if (this.$select.val() === 'tunnel-1' || this.$select.val() === 'tunnel-2') {
                $('.grid').click(handleTunnelClick);
            } else {
                $('.grid').click(handleClick);
            }
            type = this.$select.val();
        }
    });

    let btnHtml = (c, r) => '<td id="' + c + '-' + r + '" class="grid" style="width:20px; height: 20px; padding: 0; background-color: white; border: black solid 1px"></td>';
    for (let i = 0; i < h; i++) {
        $('#map').append('<tr>');

        for (let j = 0; j < w; j++) {
            $('#map').append(btnHtml(i, j));
        }
        $('#map').append('</tr>');
    }
    $('.grid').each((idx, tar) => {
        $(tar).prop('row', $(tar).attr('id').split('-')[0]);
        $(tar).prop('col', $(tar).attr('id').split('-')[1]);
        $(tar).prop('selv', false);
        $(tar).prop('selh', false);
    })
    $('.grid').click(handleClick);
    $('#import').click(() => {
        let files = document.getElementById('selectFiles').files;
        console.log(files);
        if (files.length <= 0) {
            return false;
        }

        let fr = new FileReader();

        fr.onload = function (e) {
            // console.log(e);
            let result = JSON.parse(e.target.result);
            handleImport(result);
            // let formatted = JSON.stringify(result, null, 2);
            // document.getElementById('result').value = formatted;
        };


        fr.readAsText(files.item(0));
    });

    handleReset();
});