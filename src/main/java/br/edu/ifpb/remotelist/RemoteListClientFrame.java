package br.edu.ifpb.remotelist;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Cliente Swing completo para RemoteList via RMI,
 * com todos os botões visíveis usando JToolBar.
 */
public class RemoteListClientFrame extends JFrame {
    private final RemoteList stub;
    private final JTextField listIdField    = new JTextField("lista1", 10);
    private final JTextField valueField     = new JTextField(5);
    private final JTextField indexField     = new JTextField(5);
    private final DefaultListModel<Integer> model = new DefaultListModel<>();

    public RemoteListClientFrame(String host, int port) throws Exception {
        // 1) Conectar ao RMI Registry e obter o stub
        Registry reg = LocateRegistry.getRegistry(host, port);
        stub = (RemoteList) reg.lookup("RemoteListService");

        // 2) Criar botões
        JButton bAppend  = new JButton("Append");
        JButton bGet     = new JButton("Get");
        JButton bRemove  = new JButton("Remove");
        JButton bSize    = new JButton("Size");
        JButton bRefresh = new JButton("Refresh");

        // 3) Configurar JToolBar para garantir visibilidade
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(new JLabel("List ID:"));
        toolBar.add(listIdField);
        toolBar.addSeparator(new Dimension(10, 0));
        toolBar.add(new JLabel("Value:"));
        toolBar.add(valueField);
        toolBar.addSeparator(new Dimension(10, 0));
        toolBar.add(new JLabel("Index:"));
        toolBar.add(indexField);
        toolBar.addSeparator(new Dimension(20, 0));
        toolBar.add(bAppend);
        toolBar.add(bGet);
        toolBar.add(bRemove);
        toolBar.add(bSize);
        toolBar.addSeparator(new Dimension(20, 0));
        toolBar.add(bRefresh);

        // 4) Criar a JList para exibir o estado da lista
        JList<Integer> listView = new JList<>(model);
        JScrollPane scrollPane = new JScrollPane(listView);

        // 5) Montar o Frame
        setLayout(new BorderLayout(5, 5));
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 6) Ações assíncronas via SwingWorker
        bRefresh.addActionListener(e -> refreshList());

        bAppend.addActionListener(e ->
                runInBackground(() -> {
                    stub.append(listIdField.getText(), Integer.parseInt(valueField.getText()));
                    return null;
                }, this::refreshList)
        );

        bGet.addActionListener(e ->
                runInBackground(() -> {
                    int idx = Integer.parseInt(indexField.getText());
                    int v = stub.get(listIdField.getText(), idx);
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Get(" + idx + ") = " + v)
                    );
                    return null;
                }, this::refreshList)
        );

        bRemove.addActionListener(e ->
                runInBackground(() -> {
                    int v = stub.remove(listIdField.getText());
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Removed = " + v)
                    );
                    return null;
                }, this::refreshList)
        );

        bSize.addActionListener(e ->
                runInBackground(() -> {
                    int s = stub.size(listIdField.getText());
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Size = " + s)
                    );
                    return null;
                }, this::refreshList)
        );

        // 7) Configurações finais do Frame
        setTitle("RemoteList Client");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        refreshList();  // preenche a lista ao abrir
    }

    /** Executa uma tarefa longa fora do EDT e, ao fim, roda onDone no EDT */
    private <T> void runInBackground(Callable<T> task, Runnable onDone) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception {
                return task.call();
            }
            @Override protected void done() {
                onDone.run();
            }
        }.execute();
    }

    /** Recarrega do servidor o conteúdo da lista e atualiza o JList */
    private void refreshList() {
        runInBackground(() -> {
            String id = listIdField.getText();
            int sz = stub.size(id);
            List<Integer> data = new ArrayList<>(sz);
            for (int i = 0; i < sz; i++) {
                data.add(stub.get(id, i));
            }
            SwingUtilities.invokeLater(() -> {
                model.clear();
                data.forEach(model::addElement);
            });
            return null;
        }, ()->{});
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port   = args.length > 1 ? Integer.parseInt(args[1]) : 1099;
        SwingUtilities.invokeLater(() -> {
            try {
                new RemoteListClientFrame(host, port).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao conectar: " + e);
                System.exit(1);
            }
        });
    }
}
