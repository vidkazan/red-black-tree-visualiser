#pragma once
#include "../containers/ft_map.hpp"
#include "../my_ft_containers/map.hpp"
#include "unistd.h"
#include "fstream"
#include "list"
#include "map"
#include <sstream>


typedef struct s_nodeForJson{
    std::string _key;
    std::string _keyParent;
    std::string _keyLeft;
    std::string _keyRight;
    std::string _treePosition;
    std::string _color;
} t_nodeForJson;

template<typename T, typename C>
class treeApi_std {
private:
    typedef             bool                 color_type;
    typedef             ftr::map<T,C>                 map_type;
    typedef typename    map_type::tree_type::node         node_type;


    std::string                                     _json;
    std::list<t_nodeForJson>                        _treeForJson;
    std::string                                     _nodePath;
    map_type                                        _tree;
    const std::string                               _fileName;
    std::list<std::pair<std::string, std::string> > _commandList;
    std::string                                     _cmdLine;

    void        addInputCmdToTree() {
        if(_cmdLine.empty()) {
            return;
        }
        std::ofstream treeFile;
        treeFile.open(_fileName, std::ios::app);

        size_t end = _cmdLine.find('&');
        if(end == std::string::npos || treeFile.bad()) {
            return;
        }
        std::string mode = _cmdLine.substr(0, end);
        std::string val = _cmdLine.substr(end, _cmdLine.size() - end);
        if(mode.find('=') != std::string::npos && val.find('=') != std::string::npos) {
            _commandList.insert(
					_commandList.cend(),
					std::make_pair(
							mode.substr(
									mode.find('=') + 1,
									mode.size() - mode.find('=')),
							val.substr(val.find('=') + 1,val.size() - val.find('='))
					)
			);
			if(!_commandList.back().first.empty() && !_commandList.back().second.empty()) {
				treeFile << _commandList.back().first << " " << _commandList.back().second << "\n";
            }
        }
        treeFile.close();
    };
    void        deleteSubstringFromFile(const std::string & filename,size_t start,size_t end){
        std::ifstream file(filename);
        if(file.bad())
            return;
        std::string buf = slurp(file).erase(start,end-start + 1);
        file.close();
        std::ofstream file2(filename);
        if(file2.bad())
            return;
        file2 << buf;
        file2.close();
    }
    void        parseCommandLine(const std::string & commandsString, size_t start = 0, size_t end = 0) {
        if(commandsString.empty() || commandsString.size() <= end || start > end) {
            return;
        }
        std::string cmd = commandsString.substr(start, end - start);
        size_t pos = cmd.find(' ');
        if (pos != std::string::npos) {
            _commandList.insert(_commandList.cend(),std::make_pair(cmd.substr(0, pos), cmd.substr(pos + 1, cmd.size() - pos)));
        }
    }
    void        parseCommandsString(std::string commandsString) {
        size_t start = 0;
        size_t end = commandsString.find('\n');
        while (end != std::string::npos) {
            parseCommandLine(commandsString, start, end);
            start = end + 1;
            end = commandsString.find('\n', start);
        }
        parseCommandLine(commandsString, start, end);
    }
    std::string slurp(std::ifstream& in) {
        std::ostringstream sstr;
        sstr << in.rdbuf();
        return sstr.str();
    }
    void        getCurrentTreeFromFile() {
        std::string commandsString;
        std::ifstream treeFile(_fileName);
        commandsString = slurp(treeFile);
        if(!commandsString.empty()) {
            parseCommandsString(commandsString);
        }
        treeFile.close();
    }
    void        buildTree() {
        std::list<std::pair<std::string,std::string> >::iterator it = _commandList.begin();
        if(_commandList.empty()) {
            return;
        }

        for (; it != _commandList.end(); it++) {
            std::cout << it->first << " " << it->second <<  "\n";
            if (it->first == "add") {
                _tree.insert(ftr::make_pair(stoi(it->second),stoi(it->second)));
            } else if (it->first == "erase") {
                _tree.erase(stoi(it->second));
            }
        }
    }
    void        generateJsonNode(std::list<t_nodeForJson>::iterator it){
        std::string key = "\"key\": \""+it->_key+"\"";
        std::string keyParent = "\"keyParent\": \""+it->_keyParent+"\"";
        std::string keyLeft = "\"keyLeft\": \""+it->_keyLeft+"\"";
        std::string keyRight = "\"keyRight\": \""+it->_keyRight+"\"";
        std::string color = "\"color\": \""+it->_color+"\"";
        std::string treePosition = "\"treePosition\": \""+it->_treePosition+"\"";
        _json += "{\n";
        _json += key;
        _json += ",\n";
        _json += keyParent;
        _json += ",\n";
        _json += keyLeft;
        _json += ",\n";
        _json += keyRight;
        _json += ",\n";
        _json += treePosition;
        _json += ",\n";
        _json += color;
        _json += "\n";
        _json += "},\n";
    }

    void        generateJson(){
        if(_treeForJson.empty()) {
            return;
        }
        _json += "[\n";
        for(std::list<t_nodeForJson>::iterator it = _treeForJson.begin();it!=_treeForJson.end();it++) {
            generateJsonNode(it);
        }
        _json.erase(_json.size()-2);
        _json += "\n]";
    }
    void        appendTreeForJson(std::string key,std::string keyParent,std::string keyLeft,std::string keyRight,color_type color,std::string position){
        t_nodeForJson node;

        if(color == 0) {
            node._color = "BLACK";
        } else {
            node._color = "RED";
        }
        node._key = key;
        node._keyParent = keyParent;
        node._keyLeft = keyLeft;
        node._keyRight = keyRight;
        node._treePosition = position;
        _treeForJson.push_back(node);
    }
    void        order(node_type *root){
        if(root == nullptr){
            return;
        }
        std::cout << "    std\n";
        _nodePath = "C";
        std::stack<std::pair<node_type* ,std::string> > s;
        node_type * curr = root;

        while (curr != curr->right || !s.empty()) {
            while (curr != curr->left) {
                s.push(std::make_pair(curr, _nodePath));
                curr = curr->left;
                if(curr != curr->right)
                    _nodePath += "L";
            }
            appendTreeForJson("N",std::to_string(curr->parent->value->first),"leaf","leaf", 0, _nodePath);
            std::cout << "N" << " " << _nodePath + "L" << "\n";
            if(s.empty()){
                return;
            }
            _nodePath = s.top().second;
            curr = s.top().first;
            s.pop();
            std::string keyParent = "nil";
            std::string keyLeft = "nil";
            std::string keyRight = "nil";
            if(curr->parent && curr->parent->value){
                keyParent = std::to_string(curr->parent->value->first);
            } else if(curr->parent){
                keyParent = std::to_string(curr->parent->value->first);
            }
            if(curr->left && curr->left->value){
                keyLeft = std::to_string(curr->left->value->first);
            } else if(curr->left){
                keyLeft = "leaf";
            }
            if(curr->right && curr->right->value){
                keyRight = std::to_string(curr->right->value->first);
            } else if(curr->right){
                keyRight = "leaf";
            }
			if(curr->value)
            	appendTreeForJson(std::to_string(curr->value->first),keyParent,keyLeft,keyRight, curr->colour, _nodePath);
			else
				appendTreeForJson("?",keyParent,keyLeft,keyRight, curr->colour, _nodePath);
            std::cout << curr->value->first << " " << _nodePath << "\n";
            curr = curr->right;
            _nodePath += "R";
        }
    }

public:
    treeApi_std(const std::string & cmdLine) : _fileName("currentTree_std"), _cmdLine(cmdLine) {
        map_type tree;
        _tree = tree;
        std::cout << "std:getcurrtreeftomfile\n";
        getCurrentTreeFromFile();
        std::cout << "std:addinputtree\n";
        addInputCmdToTree();
        std::cout << "std:buildtree\n";
        buildTree();
    }
    ~treeApi_std(){};

    void makeJsonFromTree(const std::string & filename) {
        std::ofstream jsonFile(filename, std::ios::trunc);
        if(!_tree.empty() && _tree.getTree().getRoot() != nullptr) {
            order(_tree.getTree().getRoot());
            generateJson();
        }
        jsonFile << _json;
        jsonFile.close();
    }
};

template<typename T, typename C>
class treeApi_ft {
private:
    typedef             ft::node_color                 color_type;
    typedef             ft::map<T,C>                   map_type;
    typedef              map_type                    tree_type;
    typedef typename    tree_type::node_type        node_type;


    std::string                                     _json;
    std::list<t_nodeForJson>                        _treeForJson;
    std::string                                     _nodePath;
    map_type                                        _tree;
    const std::string                               _fileName;
    std::list<std::pair<std::string, std::string> > _commandList;
    std::string                                     _cmdLine;

    void        addInputCmdToTree() {
        if(_cmdLine.empty()) {
            return;
        }
        std::ofstream treeFile;
        treeFile.open(_fileName, std::ios::app);

        size_t end = _cmdLine.find('&');
        if(end == std::string::npos || treeFile.bad()) {
            return;
        }
        std::string mode = _cmdLine.substr(0, end);
        std::string val = _cmdLine.substr(end, _cmdLine.size() - end);
        if(mode.find('=') != std::string::npos && val.find('=') != std::string::npos) {
            _commandList.insert(_commandList.cend(),std::make_pair(mode.substr(mode.find('=') + 1, mode.size() - mode.find('=')),val.substr(val.find('=') + 1, val.size() - val.find('='))));
            if(!_commandList.back().first.empty() && !_commandList.back().second.empty()) {
                treeFile << _commandList.back().first << " " << _commandList.back().second << "\n";
            }
        }
        treeFile.close();
    };
    void        deleteSubstringFromFile(const std::string & filename,size_t start,size_t end){
        std::ifstream file(filename);
        if(file.bad())
            return;
        std::string buf = slurp(file).erase(start,end-start + 1);
        file.close();
        std::ofstream file2(filename);
        if(file2.bad())
            return;
        file2 << buf;
        file2.close();
    }
    void        parseCommandLine(const std::string & commandsString, size_t start = 0, size_t end = 0) {
        if(commandsString.empty() || commandsString.size() <= end || start > end) {
            return;
        }
        std::string cmd = commandsString.substr(start, end - start);
        size_t pos = cmd.find(' ');
        if (pos != std::string::npos) {
            _commandList.insert(_commandList.cend(),std::make_pair(cmd.substr(0, pos), cmd.substr(pos + 1, cmd.size() - pos)));
        }
    }
    void        parseCommandsString(std::string commandsString) {
        size_t start = 0;
        size_t end = commandsString.find('\n');
        while (end != std::string::npos) {
            parseCommandLine(commandsString, start, end);
            start = end + 1;
            end = commandsString.find('\n', start);
        }
        parseCommandLine(commandsString, start, end);
    }
    std::string slurp(std::ifstream& in) {
        std::ostringstream sstr;
        sstr << in.rdbuf();
        return sstr.str();
    }
    void        getCurrentTreeFromFile() {
        std::string commandsString;
        std::ifstream treeFile(_fileName);
        commandsString = slurp(treeFile);
        if(!commandsString.empty()) {
            parseCommandsString(commandsString);
        }
        treeFile.close();
    }
    void        buildTree() {
        std::list<std::pair<std::string,std::string> >::iterator it = _commandList.begin();
        if(_commandList.empty()) {
            return;
        }
        // TODO check add duplicates
        for (; it != _commandList.end(); it++) {
            if (it->first == "add") {
                _tree.insert(ft::make_pair(stoi(it->second),stoi(it->second)));
                }
            else if (it->first == "erase") {
                _tree.erase(stoi(it->second));
            }
        }
    }
    void        generateJsonNode(std::list<t_nodeForJson>::iterator it){
        std::string key = "\"key\": \""+it->_key+"\"";
        std::string keyParent = "\"keyParent\": \""+it->_keyParent+"\"";
        std::string keyLeft = "\"keyLeft\": \""+it->_keyLeft+"\"";
        std::string keyRight = "\"keyRight\": \""+it->_keyRight+"\"";
        std::string color = "\"color\": \""+it->_color+"\"";
        std::string treePosition = "\"treePosition\": \""+it->_treePosition+"\"";
        _json += "{\n";
        _json += key;
        _json += ",\n";
        _json += keyParent;
        _json += ",\n";
        _json += keyLeft;
        _json += ",\n";
        _json += keyRight;
        _json += ",\n";
        _json += treePosition;
        _json += ",\n";
        _json += color;
        _json += "\n";
        _json += "},\n";
    }
    void        generateJson(){
        if(_treeForJson.empty()) {
            return;
        }
        _json += "[\n";
        for(std::list<t_nodeForJson>::iterator it = _treeForJson.begin();it!=_treeForJson.end();it++) {
            generateJsonNode(it);
        }
        _json.erase(_json.size()-2);
        _json += "\n]";
    }
    void        appendTreeForJson(std::string key,std::string keyParent,std::string keyLeft,std::string keyRight,color_type color,std::string position){
        t_nodeForJson node;

        if(color == 0) {
            node._color = "BLACK";
        } else {
            node._color = "RED";
        }
        node._key = key;
        node._keyParent = keyParent;
        node._keyLeft = keyLeft;
        node._keyRight = keyRight;
        node._treePosition = position;
        _treeForJson.push_back(node);
    }
    void        order(node_type *root){
        if(!root){
            return;
        }
        std::cout << "___________________\n   ft\n";
        _nodePath = "C";
        std::stack<std::pair<node_type* ,std::string> > s;
        node_type * curr = root;

        while (curr || !s.empty()) {
            while (curr) {
                s.push(std::make_pair(curr, _nodePath));
                curr = curr->left;
                if(curr)
                    _nodePath += "L";
            }
            _nodePath = s.top().second;
            curr = s.top().first;
            s.pop();
            std::string keyParent = "nil";
            std::string keyLeft = "nil";
            std::string keyRight = "nil";
            if(curr->parent && curr->parent->data){
                keyParent = std::to_string(curr->parent->data->first);
            } else if(curr->parent){
                keyParent = std::to_string(curr->parent->data->first);
            }
            if(curr->left && curr->left->data){
                keyLeft = std::to_string(curr->left->data->first);
            } else if(curr->left){
                keyLeft = "leaf";
            }
            if(curr->right && curr->right->data){
                keyRight = std::to_string(curr->right->data->first);
            } else if(curr->right){
                keyRight = "leaf";
            }
			if(curr->data) {
				appendTreeForJson(std::to_string(curr->data->first), keyParent, keyLeft, keyRight, curr->color,
								  _nodePath);
				std::cout << curr->data->first << " " << _nodePath << "\n";
			} else {
				appendTreeForJson("N", keyParent, keyLeft, keyRight, curr->color, _nodePath);
				std::cout << "N" << " " << _nodePath << "\n";
			}
            curr = curr->right;
            _nodePath += "R";
        }
    }

public:
    treeApi_ft(const std::string & cmdLine) : _tree(map_type()),_fileName("currentTree_ft"), _cmdLine(cmdLine) {
        getCurrentTreeFromFile();
        addInputCmdToTree();
        buildTree();
    }
    ~treeApi_ft(){};

    void makeJsonFromTree(const std::string & filename) {
        std::ofstream jsonFile(filename, std::ios::trunc);
        if(!_tree.empty() && _tree.root() != NULL) {
            order(_tree.root());
            generateJson();
        }
        jsonFile << _json;
        jsonFile.close();
    }
};