package test;

import com.intellij.codeInsight.CodeSmellInfo;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.DaemonProgressIndicator;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.ide.BrowserUtil;
import com.intellij.lang.Language;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.CodeSmellDetector;
import com.intellij.openapi.vcs.impl.CodeSmellDetectorImpl;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.xml.ui.PsiClassPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SearchAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final ProgressIndicator daemonIndicator = new DaemonProgressIndicator();

        List<HighlightInfo> infos =
                ((DaemonCodeAnalyzerImpl) DaemonCodeAnalyzer.getInstance(project))
                        .getFileLevelHighlights(psiFile.getProject(), psiFile);
        //                .runMainPasses(psiFile, editor.getDocument(), daemonIndicator);
        System.out.println("INFOS : " + infos);
        CodeSmellDetectorImpl detector = new CodeSmellDetectorImpl(project);
        @NotNull List<CodeSmellInfo> infoss =detector.findCodeSmells(Collections.singletonList(virtualFile));
        for (CodeSmellInfo info: infoss) {
            System.out.println(info.getSeverity() + " : " + info.getDescription() + " : " + info.getStartLine());
        }
        System.out.println("INFOSSS : " + infoss);

        //getProblems();
        /*
        final DaemonCodeAnalyzerEx analyzer =
                DaemonCodeAnalyzerEx.getInstanceEx(project);

        // ensure we get fresh results; the restart also seems to
        //  prevent the "process canceled" issue (see #30)
        PsiDocumentManager.getInstance(project).commitAllDocuments();
        analyzer.restart(psiFile);

        final DaemonProgressIndicator progress = new DaemonProgressIndicator();
        Disposer.register(e.getProject()., progress);
        // analyze!
        List infos = analyzer.runMainPasses(psiFile, editor.getDocument(), progress);
        System.out.println("INFOS : " + infos);


        /*
        PsiFile psiFile = Optional.ofNullable(e.getData(LangDataKeys.PSI_FILE)).get();
        final Project project = psiFile.getProject();
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final DaemonProgressIndicator progress = new DaemonProgressIndicator();
        DaemonCodeAnalyzerImpl codeAnalyzer = (DaemonCodeAnalyzerImpl)DaemonCodeAnalyzer.getInstance(project);
        List infos = codeAnalyzer.runMainPasses(psiFile, editor.getDocument(),
                progress);
        System.out.println("INFOS : " + infos);

        if (psiFile == null) return;
        int errorCount = 0;
        if (PsiTreeUtil.hasErrorElements(psiFile)) {
            errorCount++;
        }
        System.out.println("ERROR COUNT : " + errorCount);
        /*
        if (errorCount == 0) {
            final VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null && Objects.equals(virtualFile.getExtension(), "java")) {
                final Document document = psiFile.getViewProvider().getDocument();
                if (document != null) {
                    FileDocumentManager.getInstance().saveDocument(document);
                }
            }
        }
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Optional<PsiFile> psiFile = Optional.ofNullable(e.getData(LangDataKeys.PSI_FILE));
        final Project project = psiFile.get().getProject();
        hasErrorsInFile(editor.getDocument(), project);

        final DaemonProgressIndicator progress = new DaemonProgressIndicator();
        final Disposable disposable = Disposer.newDisposable();
        DaemonCodeAnalyzerImpl codeAnalyzer = (DaemonCodeAnalyzerImpl) DaemonCodeAnalyzer.getInstance(project);
        List infos = codeAnalyzer.runMainPasses(psiFile.get(), editor.getDocument(),
                progress);
        System.out.println("INFOS : " + infos);
        String languageTag = psiFile
                .map(PsiFile::getLanguage)
                .map(Language::getDisplayName)
                .map(String::toLowerCase)
                .map(lang -> "[" + lang + "]")
                .orElse("");


        CaretModel caretModel = editor.getCaretModel();
        String selectedText = caretModel.getCurrentCaret().getSelectedText();
        System.out.println("SELECTED TEXT : " + selectedText);
        BrowserUtil.browse("https://stackoverflow.com/search?q=" + languageTag + selectedText);
        PsiTreeUtil.hasErrorElements()
        */
    }


    private static Pair<Document, List<HighlightInfo>> getProblems() {
        final Ref<PsiFile> psiFileRef = new Ref<>();
        final Ref<Editor> editorRef = new Ref<>();
        final Ref<Document> docRef = new Ref<>();
        final Ref<Project> projectRef = new Ref<>();

        Disposable context = Disposer.newDisposable();

        Ref<List<HighlightInfo>> highlightInfoList = new Ref<>();

        ApplicationManager.getApplication().runReadAction(() -> {
            final DaemonProgressIndicator progress = new DaemonProgressIndicator();
            Disposer.register(context, progress);

            ProgressManager.getInstance().runProcess(() -> {

                final DaemonCodeAnalyzerEx analyzer =
                        DaemonCodeAnalyzerEx.getInstanceEx(projectRef.get());
                //analyzer.restart(psiFileRef.get());

                // analyze!
                highlightInfoList.set(analyzer.runMainPasses(
                        psiFileRef.get(), docRef.get(), progress));
            }, progress);
        });
        return Pair.create(docRef.get(), highlightInfoList.get());
    }

    @Nullable
    public static PsiFile findTargetFile(@NotNull String path) {
        Pair<VirtualFile, Project> data = findByAbsolutePath(path);
        return data != null ? PsiManager.getInstance(data.second).findFile(data.first) : null;
    }


    @Nullable
    public static Pair<VirtualFile, Project> findByAbsolutePath(@NotNull String path) {
        File file = new File(FileUtil.toSystemDependentName(path));
        if (file.exists()) {
            VirtualFile vFile = findVirtualFile(file);
            if (vFile != null) {
                Project project = ProjectLocator.getInstance().guessProjectForFile(vFile);
                if (project != null) {
                    return Pair.create(vFile, project);
                }
            }
        }

        return null;
    }

    @Nullable
    public static VirtualFile findVirtualFile(@NotNull final File file) {
        return ApplicationManager.getApplication().runWriteAction(new Computable<VirtualFile>() {
            @Nullable
            @Override
            public VirtualFile compute() {
                return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
            }
        });
    }

    static List doHighlighting(final Disposable context, final EditorEx editor, final PsiFile psiFile) {

        final DaemonProgressIndicator progress = new DaemonProgressIndicator();
        Disposer.register(context, progress);

        return ProgressManager.getInstance().runProcess(new Computable<List>() {

            @Override
            public List compute() {
                final Project project = psiFile.getProject();
                final DaemonCodeAnalyzerEx analyzer =
                        DaemonCodeAnalyzerEx.getInstanceEx(project);

                // ensure we get fresh results; the restart also seems to
                //  prevent the "process canceled" issue (see #30)
                PsiDocumentManager.getInstance(project).commitAllDocuments();
                analyzer.restart(psiFile);

                // analyze!
                return analyzer.runMainPasses(
                        psiFile, editor.getDocument(), progress);
            }
        }, progress);
    }

    private boolean hasErrorsInFile(@NotNull Document document, Project project) {
        // We use the IntelliJ parser and look for syntax errors in the current document.
        // We block reload if we find issues in the immediate file. We don't block reload if there
        // are analysis issues in other files; the compilation errors from the flutter tool
        // will indicate to the user where the problems are.
        /*
        final PsiErrorElement firstError = ApplicationManager.getApplication().runReadAction((Computable<PsiErrorElement>)() -> {
            final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile instanceof DartFile) {
                return PsiTreeUtil.findChildOfType(psiFile, PsiErrorElement.class, false);
            }
            else {
                return null;
            }
        });
        return firstError != null;

         */
        return true;
    }




    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();
        e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection());
    }
}