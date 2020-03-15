package espot;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class RelevancePickUI {
	/*
	 * UI that lets the user to choose the relevance of interest for catalog display
	 */	
	private Shell mainShell = null;
	ArrayList<RelevancePojo> relevancePojoList = null;
	CommonUIData commonUIData = null;
	public RelevancePickUI(CommonUIData inCommonUIData) {
		commonUIData = inCommonUIData;
	}

	public void refreshCreateArtifactUI() {
		mainShell.close();
		displayRelevancePickUI();
	}

	public void displayRelevancePickUI() {

		mainShell = new Shell(commonUIData.getESPoTDisplay(), SWT.APPLICATION_MODAL|SWT.CLOSE|SWT.TITLE|SWT.BORDER|SWT.RESIZE);
		mainShell.setImage(new Image(commonUIData.getESPoTDisplay(), commonUIData.getCommons().applicationIcon));		
		mainShell.setText("Relevance Pick");
		mainShell.setLayout(new FillLayout());
		relevancePojoList = commonUIData.getCatelogPersistenceManager()
				.readRelevances(commonUIData.getCommons().getCurrentRootNick());

		final Tree tree = new Tree(mainShell, SWT.CHECK);

		TreeItem item = null;
		TreeItem[] parentTreeItems = new TreeItem[20];
		String[] prevRelevanceNodes = new String[] { "" };
		String[] relevanceNodes;
		boolean pathChanged = false;
		for (int relevanceCount = 0; relevanceCount < relevancePojoList.size(); relevanceCount++) {
			System.out.println("relevanceList[" + relevanceCount + "]"
					+ relevancePojoList.get(relevanceCount).relevance);
			relevanceNodes = relevancePojoList.get(relevanceCount).relevance
					.split("\\\\");
			System.out.println("reached out");
			pathChanged = false;
			for (int nodeCount = 0; nodeCount < relevanceNodes.length; nodeCount++) {
				System.out.println("nodeCount = " + nodeCount);
				System.out.println("relevanceNodes.length = "
						+ relevanceNodes.length);
				System.out.println("relevanceNodes[" + nodeCount + "] = "
						+ relevanceNodes[nodeCount]);
				System.out.println("prevRelevanceNodes.length = "
						+ prevRelevanceNodes.length);
				if (relevanceCount == 0) {
					if (nodeCount < (relevanceNodes.length - 1)) {
						if (nodeCount == 0) {
							item = new TreeItem(tree, SWT.NONE);
							System.out
									.println("node added1111 at " + nodeCount);
						} else {
							item = new TreeItem(parentTreeItems[nodeCount - 1],
									SWT.NONE);
							System.out
									.println("node added2222 at " + nodeCount);
						}
						item.setText("Filler");
					} else {
						if (nodeCount == 0) {
							item = new TreeItem(tree, SWT.CHECK);
							System.out
									.println("node added3333 at " + nodeCount);
						} else {
							item = new TreeItem(parentTreeItems[nodeCount - 1],
									SWT.CHECK);
							System.out
									.println("node added4444 at " + nodeCount);
						}
						item
								.setText(relevancePojoList.get(relevanceCount).relevance);
						item
								.setChecked(relevancePojoList
										.get(relevanceCount).RelevancePicked);
						item.setData("RelevancePojo", relevancePojoList
								.get(relevanceCount));
					}
					parentTreeItems[nodeCount] = item;
				} else if (nodeCount > (prevRelevanceNodes.length - 1)
						|| !relevanceNodes[nodeCount]
								.equals(prevRelevanceNodes[nodeCount])) {
					if (nodeCount > (prevRelevanceNodes.length - 1)) {
						if (nodeCount < (relevanceNodes.length - 1)) {
							item = new TreeItem(parentTreeItems[nodeCount - 1],
									SWT.NONE);
							System.out
									.println("node added5555 at " + nodeCount);

							item.setText("Filler");
						} else {
							item = new TreeItem(parentTreeItems[nodeCount - 1],
									SWT.CHECK);
							System.out
									.println("node added6666 at " + nodeCount);
							item
									.setText(relevancePojoList
											.get(relevanceCount).relevance);
							item.setChecked(relevancePojoList
									.get(relevanceCount).RelevancePicked);
							item.setData("RelevancePojo", relevancePojoList
									.get(relevanceCount));

						}
					} else {
						if (pathChanged
								|| !relevanceNodes[nodeCount]
										.equals(prevRelevanceNodes[nodeCount])) {
							pathChanged = true;
							if (nodeCount < relevanceNodes.length - 1) {
								if (nodeCount == 0) {
									item = new TreeItem(tree, SWT.NONE);
									System.out.println("node added7777 at "
											+ nodeCount);
								} else {
									item = new TreeItem(
											parentTreeItems[nodeCount - 1],
											SWT.NONE);
									System.out.println("node added8888 at "
											+ nodeCount);
								}
								item.setText("Filler");
							} else {
								item = new TreeItem(
										parentTreeItems[nodeCount - 1],
										SWT.CHECK);
								item.setText(relevancePojoList
										.get(relevanceCount).relevance);
								item.setChecked(relevancePojoList
										.get(relevanceCount).RelevancePicked);
								item.setData("RelevancePojo", relevancePojoList
										.get(relevanceCount));
								System.out.println("node added9999 at "
										+ nodeCount);
							}
						}
					}
					parentTreeItems[nodeCount] = item;
				}
			}
			prevRelevanceNodes = relevanceNodes;
		}
		for (int parentNodeCount = 0; parentNodeCount < parentTreeItems.length
				&& parentTreeItems[parentNodeCount] != null; parentNodeCount++) {
			System.out.println("parentNodeCount = " + parentNodeCount);
			System.out.println("parentTreeItems = "
					+ parentTreeItems[parentNodeCount].getText());

			parentTreeItems[parentNodeCount].setExpanded(true);
		}

		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TreeItem item = (TreeItem) event.item;
					boolean checked = item.getChecked();
					if (item.getData("RelevancePojo") != null) {
						if (checked) {
							commonUIData.getCatelogPersistenceManager()
									.pickRelevance((RelevancePojo) item
											.getData("RelevancePojo"));
						} else {
							commonUIData.getCatelogPersistenceManager()
									.unPickRelevance((RelevancePojo) item
											.getData("RelevancePojo"));
						}
					}
				}
			}
		});

		mainShell.setData(tree);
		mainShell.pack();
		mainShell.open();

		while (!mainShell.isDisposed()) {
			if (!commonUIData.getESPoTDisplay().readAndDispatch()) {
				if (commonUIData.getArtifactDisplayOkayToContinue()) {
					commonUIData.getESPoTDisplay().sleep();
				} else {
					break;
				}
			}			
		}
	}
}