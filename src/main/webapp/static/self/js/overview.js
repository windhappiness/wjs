//http://echarts.baidu.com/doc/example/line3.html
var lineChart = echarts.init(document.getElementById("section-chart"));
var lineChartOption = {
    title : {
        text: "虚拟数据"
    },
    backgroundColor: "#fff",
    grid: {
        x: 40,
        y: 50,
        x2: 10,
        y2: 30
    },
    xAxis : [
        {
            type : "category",
            boundaryGap : false,
            data : ["周一","周二","周三","周四","周五","周六","周日"]
        }
    ],
    yAxis : [
        {
            type : "value"
        }
    ],
    series : [
        {
            name:"成交",
            type:"line",
            smooth:true,
            itemStyle: {normal: {areaStyle: {type: "default"}}},
            data:[10, 12, 21, 54, 260, 830, 710]
        },
        {
            name:"预购",
            type:"line",
            smooth:true,
            itemStyle: {normal: {areaStyle: {type: "default"}}},
            data:[30, 182, 434, 791, 390, 30, 10]
        },
        {
            name:"意向",
            type:"line",
            smooth:true,
            itemStyle: {normal: {areaStyle: {type: "default"}}},
            data:[1320, 1132, 601, 234, 120, 90, 20]
        }
    ]
};
                    
lineChart.setOption(lineChartOption);

//http://echarts.baidu.com/doc/example/map1.html
var mapChart = echarts.init(document.getElementById("section-map"));
var mapChartOption = {
    title: {
        text: "上海市",
        textStyle: {
            color: "#fff"
        }
    },
    roamController: {
        show: true,
        x: "right",
        handleColor: "#000",
        fillerColor: "#fff",
        mapTypeControl: {
            "china": false,
            "上海": true
        }
    },
    series: [{
        name: "数据名称",
        type: "map",
        mapType: "上海",
        selectedMode: "single",
        itemStyle: {
            normal: {
                borderWidth: 1,
                borderColor: "#76CAF2",
                color: "#D4E6EF",
                label: {
                    show: true
                }
            },
            emphasis: {
                borderWidth: 1,
                borderColor: "#76CAF2",
                color: "#D4E6EF",
                label: {
                    show: true
                }
            }
        },
        data: [
            {name: "崇明县",value: Math.round(Math.random()*1000)},
            {name: "宝山区",value: Math.round(Math.random()*1000)},
            {name: "嘉定区",value: Math.round(Math.random()*1000)},
            {name: "青浦区",value: Math.round(Math.random()*1000)},
            {name: "杨浦区",value: Math.round(Math.random()*1000)},
            {name: "虹口区",value: Math.round(Math.random()*1000)},
            {name: "闸北区",value: Math.round(Math.random()*1000)},
            {name: "普陀区",value: Math.round(Math.random()*1000)},
            {name: "静安区",value: Math.round(Math.random()*1000)},
            {name: "黄浦区",value: Math.round(Math.random()*1000)},
            {name: "卢湾区",value: Math.round(Math.random()*1000)},
            {name: "长宁区",value: Math.round(Math.random()*1000)},
            {name: "徐汇区",value: Math.round(Math.random()*1000)},
            {name: "浦东新区",value: Math.round(Math.random()*1000)},
            {name: "松江区",value: Math.round(Math.random()*1000)},
            {name: "闵行区",value: Math.round(Math.random()*1000)},
            {name: "金山区",value: Math.round(Math.random()*1000)},
            {name: "奉贤区",value: Math.round(Math.random()*1000)},
            {name: "南汇区",value: Math.round(Math.random()*1000)}
        ]
    }]
};
mapChart.setOption(mapChartOption);

window.onresize = function(){
    lineChart.resize();
    mapChart.resize();
};