import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class ServerWorker implements Runnable {
    private Socket socket;
    private Utilizadores users;
    private String u;
    private ReentrantLock l;
    private Condition c;

    public ServerWorker(Socket socket, Utilizadores users){
        this.socket = socket;
        this.users = users;
        this.u = new String();
        this.l = new ReentrantLock();
        this.c = l.newCondition();
    }

    public void run (){
        try{
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            int flag = 0;
            while(flag != 2) {
                if( flag == 0) {
                    flag = interpretadorLogin( in, out);
                }
                else if (flag == 1)
                    flag = interpretadorServidor( in, out);
            }
            /*int c;
            boolean b;
            do {
                c = in.readInt();
                switch (c) {
                    case 1:
                        b = users.login(in);
                        out.writeBoolean(b);
                        if(b){
                            out.writeUTF("Autenticado com sucesso");
                            out.flush();
                        } else {
                            out.writeUTF("Dados de login errados");
                            out.flush();
                        }
                        break;
                    case 2:
                        b = users.registar(in);
                        if (b) {
                            out.writeUTF("Registado com sucesso");
                            out.flush();
                        } else {
                            out.writeUTF("Nome de utilizador impossivel");
                            out.flush();
                        }
                        break;
                    case 3:
                        int total = users.getusers().size();
                        out.writeInt(total);
                        out.flush();
                        break;
                    case 4:
                        System.out.println("Entrei");
                        int res = users.quantosLoc(in);
                        out.writeInt(res);
                        out.flush();
                        break;
                    default:
                        break;
                }
            }while(c!=0);
*/
        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public  int interpretadorLogin(DataInputStream in,DataOutputStream out) throws IOException{
        int flag =0;
        int c;
        String r;
        boolean b;
        do {
            c = in.readInt();
            switch (c) {
                case 1:
                    r = users.login(in);
                    if(!r.equals("")){
                        out.writeBoolean(true);
                        this.u = r;
                        flag = 1;
                        out.writeUTF("Autenticado com sucesso");
                        out.flush();
                        users.atualizaLoc(in,u);
                        out.writeUTF("Localização atualizada!");
                        out.flush();
                    } else {
                        out.writeBoolean(false);
                        out.writeUTF("Dados de login errados");
                        out.flush();
                    }
                    break;
                case 2:
                    b = users.registar(in);
                    if (b) {
                        out.writeUTF("Registado com sucesso");
                        out.flush();
                    } else {
                        out.writeUTF("Nome de utilizador impossivel");
                        out.flush();
                    }
                    break;
                case 3:
                    int total = users.getusers().size();
                    out.writeInt(total);
                    out.flush();
                    break;
                default:
                    break;
            }
        }while(c!=0 && flag==0);

        return flag;
    }

    public  int interpretadorServidor(DataInputStream in,DataOutputStream out) throws IOException, InterruptedException {
        int flag = 1;
        int c;
        boolean b;
        do{
            c = in.readInt();
            switch(c){
                case 0:
                    out.writeUTF("Até à próxima!");
                    out.flush();
                    flag = 2;
                    break;
                case 1:
                    System.out.println("Entrei");
                    int res = users.quantosLoc(in);
                    out.writeInt(res);
                    out.flush();
                    break;
                case 2:
                    users.atualizaLoc(in,u);
                    out.writeUTF("Localização atualizada!");
                    out.flush();
                    break;
                case 3:
                    users.quero_ir(in, out);
                    break;
                default:
                    break;
            }
        }while(c!=-1 && flag == 1);
        return flag;
    }
}