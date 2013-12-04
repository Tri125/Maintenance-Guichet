package simulation;import java.awt.*;import java.awt.event.*;/** Simule le clavier de la console client */class SimClavier extends Panel{    /** Constructor     *     *  @param display l'ecran ou on affiche les characteres tapes     *  @param envelopeAcceptor - Notifie si ANNULER est presse     */    SimClavier(SimEcran display,                SimFenteEnveloppe envelopeAcceptor)    {        this.display = display;        this.envelopeAcceptor = envelopeAcceptor;                setLayout(new GridLayout(5,3));                        Button [] digitKey = new Button[10];        for (int i = 1; i < 10; i ++)        {             digitKey[i] = new Button("" + i);            add(digitKey[i]);        }                add(new Label(""));                digitKey[0] = new Button("0");        add(digitKey[0]);                add(new Label(""));                        Button enterKey = new Button("ENTRER");        enterKey.setForeground(Color.black);        enterKey.setBackground(new Color(128, 128, 255)); // Light blue        add(enterKey);                Button clearKey = new Button("EFFACER");        clearKey.setForeground(Color.black);        clearKey.setBackground(new Color(255, 128, 128)); // Light red        add(clearKey);                Button cancelKey = new Button("ANNULER");        cancelKey.setBackground(Color.red);        cancelKey.setForeground(Color.black);        add(cancelKey);                // Ajout des action listeners pour chaque boutton                for (int i = 0; i < 10; i ++)            digitKey[i].addActionListener(new ActionListener() {                public void actionPerformed(ActionEvent e)                {                    digitKeyPressed(Integer.parseInt(e.getActionCommand()));                }            });                    enterKey.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e)            {                enterKeyPressed();            }        });        clearKey.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e)            {                clearKeyPressed();            }        });        cancelKey.addActionListener(new ActionListener() {            public void actionPerformed(ActionEvent e)            {                cancelKeyPressed();            }        });                // Permet l'utilisation du clavier regulier        addKeyListener(new KeyAdapter() {            public void keyPressed(KeyEvent e)            {                char keyChar = e.getKeyChar();                int keyCode = e.getKeyCode();                if (keyChar >= '0' && keyChar <= '9')                {                    digitKeyPressed(keyChar - '0');                    e.consume();                }                else                {                    switch (keyCode)                    {                        case KeyEvent.VK_ENTER:                                                    enterKeyPressed();                            break;                                                    case KeyEvent.VK_CLEAR:                                                    clearKeyPressed();                            break;                                                    case KeyEvent.VK_CANCEL:                        case KeyEvent.VK_ESCAPE:                                                    cancelKeyPressed();                            break;                    }                    e.consume();                }            }        });        // Initialize le mode et le tmpon d'entre                currentInput = new StringBuffer();        mode = IDLE_MODE;    }        /** Lit des entrees du clavier     *     *  @param mode Le mode d'entree utilise - une des constantes definies ci-dessous.     *  @param maxValue La valeur maximale acceptee (utilisee dans MENU_MODE seulement)     *  @return La ligne qui a ete entre - null si l'usager a appuyer sur ANNULER.     */    synchronized String readInput(int mode, int maxValue)    {        this.mode = mode;        this.maxValue = maxValue;        currentInput.setLength(0);        cancelled = false;        if (mode == AMOUNT_MODE)            setEcho("0.00");        else            setEcho("");        requestFocus();                try        {            wait();        }        catch(InterruptedException e)        { }                this.mode = IDLE_MODE;                if (cancelled)            return null;        else            return currentInput.toString();    }        /** Gere une entree chiffre      *     *  @param digit La valeur de l'entree     */    private synchronized void digitKeyPressed(int digit)    {        switch (mode)        {            case IDLE_MODE:                            break;                            case PIN_MODE:            {                currentInput.append(digit);                StringBuffer echoString = new StringBuffer();                for (int i = 0; i < currentInput.length(); i ++)                    echoString.append('*');                setEcho(echoString.toString());                break;            }                            case AMOUNT_MODE:            {                           currentInput.append(digit);                String input = currentInput.toString();                if (input.length() == 1)                    setEcho("0.0" + input);                else if (input.length() == 2)                    setEcho("0." + input);                else                    setEcho(input.substring(0, input.length() - 2) + "." +                        input.substring(input.length() - 2));                break;            }                        case MENU_MODE:            {                if (digit > 0 && digit <= maxValue)                {                    currentInput.append(digit);                    notify();                }                else                    getToolkit().beep();                break;            }                          case BILL_MODE:            {                currentInput.append(digit);                setEcho(currentInput.toString());                break;            }        }    }        /** Gere l'entree ENTRER     */    private synchronized void enterKeyPressed()    {        switch(mode)        {            case IDLE_MODE:                            break;                            case PIN_MODE:            case AMOUNT_MODE:                            if (currentInput.length() > 0)                    notify();                else                    getToolkit().beep();                break;                                case MENU_MODE:                            getToolkit().beep();                break;                            case BILL_MODE:                if (currentInput.length() > 0)                    notify();                else                    getToolkit().beep();                break;        }    }                               /** Gere l'entree EFFACER     */    private synchronized void clearKeyPressed()    {        switch(mode)        {            case IDLE_MODE:                            break;                            case PIN_MODE:                            currentInput.setLength(0);                setEcho("");                break;                            case AMOUNT_MODE:                            currentInput.setLength(0);                setEcho("0.00");                break;                            case MENU_MODE:                            getToolkit().beep();                break;                            case BILL_MODE:                currentInput.setLength(0);                setEcho("");                break;        }    }                               /** Gere l'entree ANNULER     */    private synchronized void cancelKeyPressed()    {        switch(mode)        {            case IDLE_MODE:                            // Il est possible d'appuyer sur ANNULER quand on doit inserer une enveloppe            	//- alors on notifie la fente d'enveloppe de ce fait                                synchronized(envelopeAcceptor)                {                    envelopeAcceptor.notify();                }                            case PIN_MODE:            case AMOUNT_MODE:            case MENU_MODE:                            cancelled = true;                notify();        }    }                   /** Met en place le String a etre affiche sur l'ecran     *     *  @param echo Le text a etre affiche     */    private void setEcho(String echo)    {        display.setEcho(echo);    }        /** L'ecran sur lequel on affiche     */    private SimEcran display;        /** La fente enveloppe si ANNULER est appuye lorsqu'on attend pas une entree du client      */    private SimFenteEnveloppe envelopeAcceptor;        /** Mode d'entree actuel - ne des valeurs ci dessous     */    private int mode;        /** Pas de lecture d'entree en ce moment - ignorer les touches (sauf ANNULER)     */    private static final int IDLE_MODE = 0;        /** mode lire NIP  - accepter plusieurs characteres, afficher des asterisques     */    private static final int PIN_MODE = Simulation.PIN_MODE;        /** mode lire montant      */    private static final int AMOUNT_MODE = Simulation.AMOUNT_MODE;        /** mode lire choix menu - attend un chiffre comme entree et le retourne immediatement     */    private static final int MENU_MODE = Simulation.MENU_MODE;        /** mode lire numero facture     */    private static final int BILL_MODE = Simulation.BILL_MODE;        /** Lgine d'entree partielle courante     */    private StringBuffer currentInput;        /** flag d'annulatiion - mis a Vrai si client annule     */    private boolean cancelled;        /** mvaleur aximum valide - utilisee dans mode choix menu seulement     */    private int maxValue;}                                       