//
// Created by Dmitrii Grigorev on 14.09.22.
//

#include "fstream"
int main() {
    std::ofstream currentTree;
    currentTree.open("currentTree_ft",std::ios::trunc);
    currentTree.close();
    currentTree.open("currentTree_std",std::ios::trunc);
    currentTree.close();
    std::ofstream treeJson("tree.json",std::ios::trunc);
    std::ofstream treeJsonFt("tree_ft.json",std::ios::trunc);
    treeJson.close();
    treeJsonFt.close();
    return 0;
}