import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DrumMachine {
    JFrame frame; //фрейм
    JPanel panel; //панель для отображения
    ArrayList <JCheckBox> arrayList; //массив для хранения флажков
    Sequencer sequencer; //синтезатор
    Sequence sequence;  //последовательность
    Track track; //трек для проигрывания

    String[] instrumentsNames = {"Бас-бочка","Закрытый хай-хэт","Открытый хай-хэт","Малый барабан","Крэш","Хлоп","Высокий том-том","Бонго","Маракасы","Свисток","Конга","Ковбелл","Вибрирующий слэп","Низкий том-том","Высокий агого","Тамтам"};
    int[] instrumentsNumbers = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args){
        new DrumMachine().goDrums();
    }

    public void goDrums(){
        frame = new JFrame("DrumMachine"); //создаем фрейм, даем ему имя
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//разрешаем закрытие по клику
        BorderLayout layout = new BorderLayout(); //создаем диспетчер компоновки по границе
        JPanel background = new JPanel(layout); //запускаем панель с диспетчером по границе
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); //указываем отстуты панели от границ фрейма в 10 единиц

        arrayList = new ArrayList<>(); //массив для чекбоксов
        Box buttonBox = new Box(BoxLayout.Y_AXIS); //контейнер для хранения элементов с диспетчером BoxLayout (Класс Box реализует контейнер с компоновщиком BoxLayout)

        JButton start = new JButton("Начать"); //создаем кнопку
        start.addActionListener(new MyStartListener()); //назначаем ей слушателя, который будет запускать выполнение кода
        buttonBox.add(start); //добавляем кнопку в контейнер

        JButton stop = new JButton("Остановите!"); //создаем кнопку
        stop.addActionListener(new MyStopListener()); //назначаем ей слушателя, который будет останавливать выполнение кода
        buttonBox.add(stop); //добавляем кнопку в контейнер

        JButton stopDouble = new JButton("Остановите!"); //создаем кнопку
        stop.addActionListener(new MyStopListener()); //назначаем ей слушателя, который будет останавливать выполнение кода
        buttonBox.add(stopDouble); //добавляем кнопку в контейнер

        JButton upTempo = new JButton("Выше темп");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo); //здесь все аналогично предыдущим 2 кнопкам

        JButton downTempo = new JButton("Ниже темп");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo); //тут аналогично предыдущим 3 кнопкам

        Box nameBox = new Box(BoxLayout.Y_AXIS); //создаем контейнер для названий инструментов
        for (int a = 0; a < 16; a++){nameBox.add(new Label(instrumentsNames[a]));} //добавляем названия инструментов в контейнер

        background.add(BorderLayout.EAST, buttonBox); //добавляем на панель (восток) контейнер с кнопками
        background.add(BorderLayout.WEST, nameBox); //добаляем на панель (запад) контейнер с названиями инструментов

        frame.getContentPane().add(background); //добавляем блок с визуалом (JPanel) во фрейм

        GridLayout gridLayout = new GridLayout(16, 16); //создаем новый диспетчер компоновки для расположения ячеек в виде таблицы (ячейки в ней будут одинакового размера)
        gridLayout.setVgap(1); //пока не знаю, что эти строки значат
        gridLayout.setHgap(2); //пока не знаю, что эти строки значат
        panel = new JPanel(gridLayout); //добавляем в главный фрейм расположение элементов грид (табличка)

        background.add(BorderLayout.CENTER, panel); //вставляем главную панель в бэкграунд по центру

        for (int a = 0; a < 256; a++){ //заполняем центральную панель чекбоксами, заодно добавляем их в массив арейлист
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(false);
            arrayList.add(checkBox);
            panel.add(checkBox);
        }

        makeMidi();

        frame.setVisible(true);
        frame.setBounds(50, 50, 300,300); //setBounds дополнительно указывает отстут от левого верхнего угла экрана для всего всейма
        frame.pack();//метод pack устанавливает минимальный необходимый размер фрейма
    }

    public void makeMidi(){ //здесь просто создается трек и задается для него скорость воспроизведения
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ,4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120); //сколько битов в минуту - темп воспроизведения
        }
        catch (Exception e){e.printStackTrace();}
    }

    public void buildTrackAndStart(){ //метод для создания мелодии на основе графики
        int [] trackList = null; //создаем массив для хранения значений инструментов - для каждого инструкмента свой массив

        sequence.deleteTrack(track);//чистим трэк
        track = sequence.createTrack();//создаем новый

        for (int a = 0; a < 16; a++){ //первый обход - выгягиваем инструмент, сохраняем его в переменную
            trackList = new int[16]; //каждый раз новый массив для звучания
            int key = instrumentsNumbers[a];//заходим в массив с инструментами и вытягиываем по очереди инструменты, сохраняем в переменную

            for(int b = 0; b < 16; b++){ //второй обход - вытягиваем значение чекбокса из массива
                JCheckBox jc = arrayList.get(b + (16*a));//всего для инструмента 16 чекбоксовых значений, умножая номер инструмента в массиве на 16 мы переходим к следубщей шестнадцатке
                if (jc.isSelected()) trackList[b] = key; //если чекбокс отмечен,то инструмент помещается в массив (звучит)
                else trackList[b] = 0; //если чекбокс не отмечен, то ничего не происходит
            }
            makeTracks(trackList); //cоздаем события для текущего инструмента (в каких тактах должен играть) и отправляем на создание событий и трека
            track.add(makeEvent(176,1,127,0,16)); //ПОКА НЕ ЗНАЮ ЧТО ЗНАЧИТ
        }
        track.add(makeEvent(192, 9, 1, 0, 15));//ПОКА НЕ ЗНАЮ ЧТО ЗНАЧИТ
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }
        catch (Exception e){e.printStackTrace();}
    }

    public void makeTracks(int[] list){//здесь делается трек, который будет звучать в такте
        for (int a = 0; a < 16; a++){ //мы получаем массив на 16 единиц, где находится либо номер инструмента, либо 0 (обход идет по оси игрек)
            int key = list[a];

            if(key != 0){//если ячейка в массиве не ноль
                track.add(makeEvent(144, 9, key, 100, a));//создаем событие - старт игры на инструменте
                track.add(makeEvent(128, 9, key, 100, a+1));//создаем событие - стоп игры на инструменте в следующем такте (а = такт)
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int trick){ //здесь создается событие - передается инфа из МейкТрекс
        MidiEvent event = null;
        try{
            ShortMessage sm = new ShortMessage();
            sm.setMessage(comd, chan, one, two); //просто создается сообщение - старт или стоп, канал, ИНСТРУМЕНТ, сила звучания
            event = new MidiEvent(sm, trick); //передается сообщение + когда оно звучит (очередность - на 0 позиции или на 16)
        }
        catch (Exception e) {e.printStackTrace();}
        return event;
    }

    public class MyStartListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 0.97));
        }
    }
}