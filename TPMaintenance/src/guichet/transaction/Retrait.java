package guichet.transaction;import compte.InformationCompte;import compte.Carte;import compte.Message;import compte.Argent;import compte.Recu;import compte.Etat;import guichet.Guichet;import guichet.Session;import guichet.physique.*;/** Representation d'une transaction de retrait */public class Retrait extends Transaction{	 /** Constructor    *    *  @param atm le guichet utilise pour communiquer avec le client    *  @param session la session dans laquelle la transaction est realisee    *  @param card la carte client    *  @param pin le NIP entre par le client    */    public Retrait(Guichet atm, Session session, Carte card, int pin)    {        super(atm, session, card, pin);    }        /** Recupere des details de la transaction avec le client    *    *  @return message a la banque pour initier cette transaction     *  @exception ConsoleClient.Cancelled si le client annule la transaction    */    protected Message getSpecificsFromCustomer() throws ConsoleClient.Cancelled    {        from = atm.getCustomerConsole().readMenuChoice(            "Compte a partir duquel on retire",            InformationCompte.ACCOUNT_NAMES);        String [] amountOptions = { "$20", "$40", "$60", "$100", "$200" };        Argent [] amountValues = {                                   new Argent(20), new Argent(40), new Argent(60),                                  new Argent(100), new Argent(200)                                };                                          String amountMessage = "";        boolean validAmount = false;                while (! validAmount)        {            amount = amountValues [                 atm.getCustomerConsole().readMenuChoice(                    amountMessage + "Montant d'argent a retirer", amountOptions) ];                                        validAmount = atm.getCashDispenser().checkCashOnHand(amount);            if (! validAmount)                amountMessage = "Fonds insuffisants\n";        }                return new Message(Message.WITHDRAWAL,                            card, pin, serialNumber, from, -1, amount);    }        /** Complete une transaction approuvee    *    *  @return recu a etre imprimer pour cette transaction    */    protected Recu completeTransaction()    {        atm.getCashDispenser().dispenseCash(amount);        return new Recu(this.atm, this.card, this, this.balances) {            {                detailsPortion = new String[2];                detailsPortion[0] = "RETRAIT Du: " +                                     InformationCompte.ACCOUNT_ABBREVIATIONS[from];                detailsPortion[1] = "Montant: " + amount.toString();            }        };    }        /** Compte duquel on retire     */     protected int from;        /** Montant d'argent a retier     */    protected Argent amount;           }