package controller;

import entities.Aluno;
import entities.Curso;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Programa {

    public static void main(String[] args) {
        // Criação de uma 'EntityManagerFactory'. Este é um passo caro, normalmente feito apenas uma vez por aplicação.
        // "exemplo-jpa" é o nome da unidade de persistência definida no arquivo "persistence.xml".
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("TDE2");

        // O 'EntityManager' é responsável por gerenciar as entidades e suas transações.
        // É criado a partir da 'EntityManagerFactory' e é uma instância leve que deve ser usada e descartada para cada transação ou série de transações.
        EntityManager em = emf.createEntityManager();

        try {
            // Iniciar uma transação. É necessário para realizar operações de persistência, como salvar um objeto no banco de dados.

            int opn = 1;

            //Loop com um menu de opções, que só é encerrado se o usuário aperta 0
            while (opn != 0) {
                System.out.println("Digite 1 para adicionar um aluno");
                System.out.println("Digite 2 para adicionar um curso");
                System.out.println("Digite 3 para mostrar todos os cursos");
                System.out.println("Digite 4 para mostrar todos os alunos");
                System.out.println("Digite 5 para adicionar um aluno a determinado curso");
                System.out.println("Digite 6 para mostrar todos os alunos e seus respectivos cursos");
                System.out.println("Digite 7 para mostrar todos os alunos de determinado curso");


                System.out.println("Digite 0 para sair");

                Scanner scan = new Scanner(System.in);
                opn = scan.nextInt();
                switch (opn) {

                    //Inserindo aluno
                    case 1:
                        System.out.println("Digite o número da matrícula do aluno:");
                        String matricula = scan.next();

                        System.out.println("Digite o nome do aluno:");
                        String nome = scan.next();


                        Aluno aluno = new Aluno(matricula, nome);

                        inserirAluno(emf, aluno);


                        break;

                    //Inserindo curso
                    case 2:

                        System.out.println("Digite o código do curso:");
                        int codigoCurso = scan.nextInt();

                        System.out.println("Digite o nome do curso:");
                        String nomeCurso = scan.next();

                        System.out.println("Digite a carga horária do curso:");
                        int cargaHoraria = scan.nextInt();

                        Curso curso = new Curso(codigoCurso, nomeCurso, cargaHoraria);

                        inserirCurso(emf, curso);


                        break;

                    case 3:
                        //Mostrando todos os cursos do banco de dados

                        try {
                            mostraCursos(emf);
                        }catch(Exception e){
                            e.printStackTrace();
                        }


                        break;


                    case 4:
                        //Mostrando todos os alunos do banco de dados

                        try {
                            mostraAlunos(emf);
                        }catch(Exception e){
                            e.printStackTrace();
                        }


                        break;



                    case 5:

                        try {


                            String nomeDoCurso;
                            String matriculaAluno;

                            Scanner scan2 = new Scanner(System.in);

                            System.out.println("Escreva a matrícula do aluno que você deseja alocar");
                            mostraAlunos(emf);
                            matriculaAluno = scan2.next();

                            Aluno a1 = buscaAlunoPelaMatricula(emf, matriculaAluno);

                            System.out.println("Selecione o nome do curso");
                            mostraCursos(emf);
                            nomeDoCurso = scan2.next();

                            Curso c1 = buscaCursoPeloNome(emf, nomeDoCurso);

                            em.getTransaction().begin();
                            c1.getAlunos().add(a1);
                            em.persist(c1);
                            em.getTransaction().commit();

                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        break;





                    case 6:
                        //Mostrando todos os alunos e seus cursos

                        try {
                            mostraAlunosECurso(emf);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        break;


                    case 7:
                        //Mostrando todos os alunos de determinado curso

                        try {
                            Scanner scan4 = new Scanner(System.in);
                            System.out.println("Digite o nome do curso");
                            String cursoEscolhido = scan4.next();
                            mostraAlunosDeCurso(emf, cursoEscolhido);
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        break;

                }

            }
        } catch (Exception e) {
            // Se houver alguma exceção durante a transação, faz o rollback para evitar um estado inconsistente no banco de dados.
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            // Fechamento do 'EntityManager'. É importante fechar para liberar os recursos que ele está consumindo.
            em.close();
        }

        // Fechamento da 'EntityManagerFactory'. Uma vez fechado, nenhum 'EntityManager' pode ser criado. Deve ser fechado ao final do programa para liberar recursos.
        emf.close();
    }

    public static void inserirAluno(EntityManagerFactory emf, Aluno a1) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(a1);
            // Commit da transação. Isso confirmará as operações de persistência realizadas durante a transação.
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            em.close();
        }

    }


    public static void inserirCurso(EntityManagerFactory emf, Curso c1) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(c1);
            // Commit da transação. Isso confirmará as operações de persistência realizadas durante a transação.
            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            em.close();
        }

    }


    public static void mostraCursos(EntityManagerFactory emf) {


        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {

            transaction.begin();

            Query query = em.createQuery("select c from Curso c");
            List<Curso> resultList = query.getResultList();

            for(Curso x: resultList){
                System.out.println(x);
            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            em.close();
        }

    }


    public static void mostraAlunosECurso(EntityManagerFactory emf) {


        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Query query = em.createQuery("SELECT a FROM Aluno a JOIN a.cursos c");
            List<Aluno> resultList = query.getResultList();

            Query query2 = em.createQuery("SELECT c.nome FROM Aluno a JOIN a.cursos c");
            List<String> resultList2 = query2.getResultList();

            int i = 0;
            for(Aluno x: resultList){

                System.out.print(x);
                System.out.println(" {" + resultList2.get(i) + "}");
                i = i+1;

            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            em.close();
        }

    }


    public static void mostraAlunos(EntityManagerFactory emf) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {

            transaction.begin();

            Query query = em.createQuery("SELECT a FROM Aluno a");
            List<Aluno> resultList = query.getResultList();

            for(Aluno x: resultList){

                System.out.println(x);



            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            em.close();
        }

    }


    public static Aluno buscaAlunoPelaMatricula(EntityManagerFactory emf, String matricula) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Query query = em.createQuery("SELECT a FROM Aluno a WHERE a.matricula = :matricula");
            query.setParameter("matricula", matricula);

            List<Aluno> resultList = query.getResultList();

            Aluno a1 = resultList.get(0);

            return a1;



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            em.close();
        }
        return null;

    }


    public static Curso buscaCursoPeloNome(EntityManagerFactory emf, String nomeCurso) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Query query = em.createQuery("SELECT c FROM Curso c WHERE c.nome = :nomeCurso");
            query.setParameter("nomeCurso", nomeCurso);

            List<Curso> resultList = query.getResultList();

            Curso c1 = resultList.get(0);

            return c1;



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            em.close();
        }
        return null;

    }



    public static void mostraAlunosDeCurso(EntityManagerFactory emf, String nomeCurso) {


        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Query query = em.createQuery("SELECT a FROM Aluno a JOIN a.cursos c WHERE c.nome = :nomeCurso");
            query.setParameter("nomeCurso", nomeCurso);
            List<Aluno> resultList = query.getResultList();



            for(Aluno x: resultList){

                System.out.print(x);

            }

            transaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            em.close();
        }

    }



}






























