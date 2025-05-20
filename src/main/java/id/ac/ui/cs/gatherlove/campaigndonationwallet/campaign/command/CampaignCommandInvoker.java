package id.ac.ui.cs.gatherlove.campaigndonationwallet.command;

public class CampaignCommandInvoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void run() {
        if (command != null) {
            command.execute();
        }
    }
}
